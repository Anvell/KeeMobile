package io.github.anvell.keemobile.core.permissions

interface PermissionsProxy {

    fun requestPermissions(permissions: Array<out String>, requestCode: Int)
}
