package zoomapps.mobileoko

import android.app.Application
import di.AppModule
import di.component.AppComponent
import di.component.DaggerAppComponent

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule())
                .build()
    }
    companion object {
        internal lateinit var appComponent: AppComponent
    }
}
