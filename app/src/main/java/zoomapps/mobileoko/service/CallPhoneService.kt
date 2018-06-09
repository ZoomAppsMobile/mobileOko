package zoomapps.mobileoko.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.URISyntaxException
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import zoomapps.mobileoko.R
import zoomapps.mobileoko.activity.MainActivity
import java.util.*


open class CallPhoneService(name: String?) : IntentService(name) {
    constructor() : this("")

    var mContex: Context? = null
    private var mSocket: Socket? = null
    override fun onHandleIntent(intent: Intent?) {

    }


    override fun onDestroy() {
        super.onDestroy()
        mSocket?.disconnect()
        Toast.makeText(this, "Сервис остановлен", Toast.LENGTH_LONG).show()
        EventBus.getDefault().unregister(this)

    }


    override fun onCreate() {
        super.onCreate()
        mContex = applicationContext
        EventBus.getDefault().register(this)
        startService(Intent(this, USSDService::class.java))
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: String) {
//        Убрать иконку приложения // COMPONENT_ENABLED_STATE_ENABLED // показать
//        val p = packageManager
//        val componentName = ComponentName(this, MainActivity::class.java)
//        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)


        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
        val resultIntent = Intent(this, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(Date().time.toInt(), mBuilder.build())
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val opts = IO.Options()
            opts.reconnection = true
            mSocket = IO.socket("http://192.168.1.6:3000", opts)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
        initSocket()
        mSocket!!.on("testSocket".toString(), testSocket)
        return START_STICKY
    }

    var testSocket = Emitter.Listener { array ->
        EventBus.getDefault().post("4321")
    }


    fun initSocket() {
        mSocket!!.connect()

    }


}