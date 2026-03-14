package com.johannjara.docvault.core.security

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ScreenSecurityHelperTest {

    @Test
    fun `setScreenSecurity should add FLAG_SECURE when isEnabled is true`() {
        val activity = mockk<Activity>()
        val window = mockk<Window>()
        every { activity.window } returns window
        every { window.addFlags(any()) } returns Unit

        ScreenSecurityHelper.setScreenSecurity(activity, true)

        verify { window.addFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }

    @Test
    fun `setScreenSecurity should clear FLAG_SECURE when isEnabled is false`() {
        val activity = mockk<Activity>()
        val window = mockk<Window>()
        every { activity.window } returns window
        every { window.clearFlags(any()) } returns Unit

        ScreenSecurityHelper.setScreenSecurity(activity, false)

        verify { window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }
}
