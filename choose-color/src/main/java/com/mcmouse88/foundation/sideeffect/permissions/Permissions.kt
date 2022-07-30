package com.mcmouse88.foundation.sideeffect.permissions

import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.sideeffect.permissions.plugin.PermissionStatus

interface Permissions {

    fun hasPermissions(permission: String): Boolean

    fun requestPermission(permission: String): Task<PermissionStatus>
}