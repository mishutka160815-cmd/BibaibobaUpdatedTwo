package com.bibaiboba.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class BibaibobaAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Reserved for future device automation actions triggered by Bibaiboba.
    }

    override fun onInterrupt() {
        // No-op
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    companion object {
        var instance: BibaibobaAccessibilityService? = null
            private set

        fun isConnected(): Boolean = instance != null
    }
}
