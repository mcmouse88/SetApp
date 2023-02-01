package com.mcmouse88.catadapterespresso.robolectric.test_utils.image_loader

import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

/**
 * Класс который подменяет загрузчик картинок в тестах, вместо того, чтобы рисовать черные квадраты,
 * как это делал Espresso, здесь просто будут проверятся на соответствие ссылки на картинку
 */
class FakeImageLoader : ImageLoader {

    override val components = ComponentRegistry()
    override val defaults = DefaultRequestOptions()
    override val diskCache: DiskCache? = null
    override val memoryCache: MemoryCache? = null

    override fun enqueue(request: ImageRequest): Disposable {
        val url = request.data.toString()
        request.target?.onStart(request.placeholder)
        val drawable = createDrawable(url)
        request.target?.onSuccess(drawable)
        return object : Disposable {

            override val isDisposed: Boolean = true

            override val job: Deferred<ImageResult>
                get() = CompletableDeferred(newResult(request, url))

            override fun dispose() {
                /* no-op */
            }
        }
    }

    override suspend fun execute(request: ImageRequest): ImageResult {
        val url = request.data.toString()
        return newResult(request, url)
    }

    override fun newBuilder(): ImageLoader.Builder {
        throw UnsupportedOperationException()
    }

    override fun shutdown() {
        /* no-op */
    }

    private fun newResult(request: ImageRequest, url: String): SuccessResult {
        return SuccessResult(
            drawable = createDrawable(url),
            request = request,
            dataSource = DataSource.MEMORY_CACHE
        )
    }

    companion object {
        fun createDrawable(url: String) = FakeImageLoaderDrawable(url)
    }
}