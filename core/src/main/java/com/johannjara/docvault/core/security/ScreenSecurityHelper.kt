package com.johannjara.docvault.core.security

import android.app.Activity
import android.view.WindowManager

object ScreenSecurityHelper {
    fun setScreenSecurity(activity: Activity, isEnabled: Boolean) {
        if (isEnabled) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
