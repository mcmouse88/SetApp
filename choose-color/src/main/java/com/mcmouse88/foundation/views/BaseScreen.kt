package com.mcmouse88.foundation.views

import java.io.Serializable

interface BaseScreen : Serializable {
    companion object {
        const val ARG_SCREEN = "ARG_SCREEN"
    }
}