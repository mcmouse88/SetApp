package com.mcmouse88.cats_adapter_espresso.apps.fragments

interface FragmentRouter {

    fun goBack()

    fun showDetail(catId: Long)
}