package mx.edu.utez.deal.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionChecker (private val activity: Activity) {

    private val requestCode: Int = 200

    var permissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity.applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPermissionsNotGranted(): Array<String> {
        return this.permissions.filter { permission: String -> isPermissionGranted(permission).not() }
            .toTypedArray()
    }


    fun arePermissionsGranted(): Boolean {
        return this.permissions.all { permission: String -> isPermissionGranted(permission) }
    }


    fun requestPermissions() {
        ActivityCompat.requestPermissions(activity, getPermissionsNotGranted(), this.requestCode)
    }
}