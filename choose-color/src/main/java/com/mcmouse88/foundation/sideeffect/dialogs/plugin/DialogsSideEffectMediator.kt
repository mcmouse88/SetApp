package com.mcmouse88.foundation.sideeffect.dialogs.plugin

import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.model.tasks.callback.CallbackTask
import com.mcmouse88.foundation.model.tasks.callback.Emitter
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.dialogs.Dialogs

class DialogsSideEffectMediator : SideEffectMediator<DialogsSideEffectImpl>(), Dialogs {

    var retainedState = RetainedState()

    override fun show(dialogConfig: DialogConfig): Task<Boolean> = CallbackTask.create { emitter ->
        if (retainedState.record != null) {
            emitter.emit(ErrorResult(IllegalStateException("Can't launch more then one dialog at a time")))
            return@create
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

    class DialogRecord(
        val emitter: Emitter<Boolean>,
        val config: DialogConfig
    )

    class RetainedState(
        var record: DialogRecord? = null
    )
}