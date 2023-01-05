package com.mcmouse88.foundation.sideeffect.permissions.plugin

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.mcmouse88.foundation.model.Emitter
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.toEmitter
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.permissions.Permissions
import kotlinx.coroutines.suspendCancellableCoroutine

class PermissionsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<PermissionsSideEffectImpl>(), Permissions {

    val retainedState = RetainedState()

    override fun hasPermissions(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(permission: String): PermissionStatus = suspendCancellableCoroutine { continuation ->
        val emitter = continuation.toEmitter()
        if (retainedState.emitter != null) {
            emitter.emit(ErrorResult(IllegalStateException("Only one permission request can be active")))
            return@suspendCancellableCoroutine
        }
        retainedState.emitter = emitter
        target { implementation ->
            implementation.requestPermission(permission)
        }
    }

    class RetainedState(
        var emitter: Emitter<PermissionStatus>? = null
    )
}