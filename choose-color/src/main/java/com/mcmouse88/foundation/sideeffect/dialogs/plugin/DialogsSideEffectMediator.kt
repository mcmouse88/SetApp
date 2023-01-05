package com.mcmouse88.foundation.sideeffect.dialogs.plugin

import com.mcmouse88.foundation.model.Emitter
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.toEmitter
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.dialogs.Dialogs
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Данный класс работает на стороне ViewModel в качестве посредника, а для реализации мы
 * используем класс [DialogsSideEffectImpl]
 */
class DialogsSideEffectMediator : SideEffectMediator<DialogsSideEffectImpl>(), Dialogs {

    var retainedState = RetainedState()

    override suspend fun show(dialogConfig: DialogConfig): Boolean = suspendCancellableCoroutine { continuation ->
        val emitter = continuation.toEmitter()
        if (retainedState.record != null) {
            emitter.emit(ErrorResult(IllegalStateException("Can't launch more then one dialog at a time")))
            return@suspendCancellableCoroutine
        }

        val wrappedEmitter = Emitter.wrap(emitter) {
            retainedState.record = null
        }

        val record = DialogRecord(wrappedEmitter, dialogConfig)
        wrappedEmitter.setCancelListener {
            target { implementation ->
                implementation.removeDialog()
            }
        }

        target { implementation ->
            implementation.showDialog(record)
        }

        retainedState.record = record
    }

    /**
     * Два класса для сохранения состояния диалога при смене конфигурации.
     */
    class DialogRecord(
        val emitter: Emitter<Boolean>,
        val config: DialogConfig
    )

    class RetainedState(
        var record: DialogRecord? = null
    )
}