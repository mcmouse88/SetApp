package com.mcmouse88.mock_k

interface ErrorHandler<R> {

    fun onError(exception: Exception, resource: R)
}