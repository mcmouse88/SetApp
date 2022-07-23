package com.mcmouse88.foundation

import com.mcmouse88.foundation.model.Repository

interface BaseApplication {

    val repositories: List<Repository>
}