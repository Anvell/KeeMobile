package io.github.anvell.keemobile.common.permissions

interface PermissionsProxy {

    fun requestPermissions(permissions: Array<out String>, requestCode: Int)
}
