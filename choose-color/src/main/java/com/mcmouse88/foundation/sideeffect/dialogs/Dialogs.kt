package com.mcmouse88.foundation.sideeffect.dialogs

import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.sideeffect.dialogs.plugin.DialogConfig

interface Dialogs {
    fun show(dialogConfig: DialogConfig): Task<Boolean>
}