package com.mcmouse88.foundation.sideeffect.permissions

import com.mcmouse88.foundation.sideeffect.permissions.plugin.PermissionStatus

interface Permissions {

    fun hasPermissions(permission: String): Boolean

    suspend fun requestPermission(permission: String): PermissionStatus
}