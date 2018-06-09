package zoomapps.mobileoko.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class USSDService : AccessibilityService() {
    override fun onInterrupt() {

    }

    val TAG = "USSDService"

    @Override
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent")
        val text = event.text.toString()
        Toast.makeText(this, "${text}", Toast.LENGTH_LONG).show()
        Log.e(TAG, text)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")
        val info =  AccessibilityServiceInfo()
        info.flags = AccessibilityServiceInfo.DEFAULT
        info.packageNames = arrayOf( "com.android.phone" )
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        serviceInfo = info
    }
}


