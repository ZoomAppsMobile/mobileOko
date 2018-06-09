package zoomapps.mobileoko.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import zoomapps.mobileoko.R
import java.util.concurrent.TimeUnit
import io.reactivex.schedulers.Schedulers
import zoomapps.mobileoko.service.CallPhoneService


class SplashScreen : AppCompatActivity() {
    var TIMER_VALUE = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stopService(Intent(this, CallPhoneService::class.java))
        setContentView(R.layout.activity_splash_screen)
        val timerDisposable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .take(3)
                .map { v -> 3 - v }
                .subscribe(
                        { onNext ->
                            if (onNext == 1.toLong()) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                this.finish()
                            }
                        },
                        { onError ->
                            //do on error
                        },
                        {
                            //do on complete
                        }
                )
    }
}
