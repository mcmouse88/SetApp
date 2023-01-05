package com.mcmouse88.foundation.model

typealias CancelListener = () -> Unit

interface Emitter<T> {

    fun emit(finalResult: FinalResult<T>)

    fun setCancelListener(cancelListener: CancelListener)

    companion object {
        fun<T> wrap(emitter: Emitter<T>, onFinish: () -> Unit): Emitter<T> {
            return object : Emitter<T> {

                override fun emit(finalResult: FinalResult<T>) {
                    onFinish()
                    emitter.emit(finalResult)
                }

                override fun setCancelListener(cancelListener: CancelListener) {
                    emitter.setCancelListener {
                        onFinish()
                        cancelListener()
                    }
                }
            }
        }
    }
}