package di

import api.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {
    var httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()


    @Provides
    @Singleton
    public fun apiService(): ApiService {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        var okHttpClient = OkHttpClient()
        var httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(httpLoggingInterceptor)

        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.1.7:3000/")
                .client(httpClient.build())
                .build()
                .create(ApiService::class.java)
    }

}