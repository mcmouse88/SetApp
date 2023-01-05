package com.mcmouse88.foundation.sideeffect.dialogs

import com.mcmouse88.foundation.sideeffect.dialogs.plugin.DialogConfig

interface Dialogs {
    suspend fun show(dialogConfig: DialogConfig): Boolean
}