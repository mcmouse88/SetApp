package com.mcmouse88.fragment_from_listview.model

import java.io.Serializable

data class Cat(
    val id: Int,
    val name: String,
    val description: String
) : Serializable {
    override fun toString(): String {
        return name
    }
}
