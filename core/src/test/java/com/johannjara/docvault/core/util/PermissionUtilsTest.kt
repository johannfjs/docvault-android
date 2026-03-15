package com.johannjara.docvault.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class PermissionUtilsTest {

    private val context: Context = mockk()

    @Before
    fun setup() {
        mockkStatic(ContextCompat::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(ContextCompat::class)
    }

    @Test
    fun `getCameraPermissions should return CAMERA permission`() {
        val permissions = PermissionUtils.getCameraPermissions()
        permissions.size shouldBeEqualTo 1
        permissions shouldContain Manifest.permission.CAMERA
    }

    @Test
    fun `hasCameraPermission should return true when permission is granted`() {
        every {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
        } returns PackageManager.PERMISSION_GRANTED

        PermissionUtils.hasCameraPermission(context) shouldBeEqualTo true
    }

    @Test
    fun `hasCameraPermission should return false when permission is denied`() {
        every {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
        } returns PackageManager.PERMISSION_DENIED

        PermissionUtils.hasCameraPermission(context) shouldBeEqualTo false
    }

    @Test
    fun `hasPermissions should return true only when all permissions are granted`() {
        val permissions = arrayOf("perm1", "perm2")
        every {
            ContextCompat.checkSelfPermission(
                context,
                "perm1"
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                context,
                "perm2"
            )
        } returns PackageManager.PERMISSION_GRANTED

        PermissionUtils.hasPermissions(context, permissions) shouldBeEqualTo true
    }

    @Test
    fun `hasPermissions should return false if any permission is denied`() {
        val permissions = arrayOf("perm1", "perm2")
        every {
            ContextCompat.checkSelfPermission(
                context,
                "perm1"
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                context,
                "perm2"
            )
        } returns PackageManager.PERMISSION_DENIED

        PermissionUtils.hasPermissions(context, permissions) shouldBeEqualTo false
    }

    @Test
    fun `getStoragePermissions should return READ_EXTERNAL_STORAGE for SDK below Tiramisu`() {
        setSdkInt(Build.VERSION_CODES.M)
        val permissions = PermissionUtils.getStoragePermissions()
        permissions shouldContain Manifest.permission.READ_EXTERNAL_STORAGE
    }

    @Test
    fun `getStoragePermissions should return media permissions for SDK Tiramisu or above`() {
        setSdkInt(Build.VERSION_CODES.TIRAMISU)
        val permissions = PermissionUtils.getStoragePermissions()
        permissions shouldContain Manifest.permission.READ_MEDIA_IMAGES
        permissions shouldContain Manifest.permission.READ_MEDIA_VIDEO
    }

    private fun setSdkInt(value: Int) {
        val field = Build.VERSION::class.java.getField("SDK_INT")
        field.isAccessible = true

        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

        field.set(null, value)
    }
}
