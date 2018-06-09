package zoomapps.mobileoko.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val data = intent?.getStringExtra("data")
    }
}