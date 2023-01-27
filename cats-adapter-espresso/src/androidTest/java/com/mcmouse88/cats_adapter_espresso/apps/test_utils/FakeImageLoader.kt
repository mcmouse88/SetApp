package com.mcmouse88.cats_adapter_espresso.apps.test_utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toDrawable
import androidx.test.core.app.ApplicationProvider
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

/**
 * Класс, который вместо загрузки изображения по сети формирует свои упращенные изображенные
 * локально (рисуется черный квадрат с текстом ссылки).
 */
class FakeImageLoader : ImageLoader {

    override val components: ComponentRegistry = ComponentRegistry()

    override val defaults: DefaultRequestOptions = DefaultRequestOptions()

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

            override fun dispose() { /* no-op */ }
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
            drawable = createDrawable(url = url),
            request = request,
            dataSource = DataSource.MEMORY_CACHE
        )
    }

    companion object {
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.YELLOW
            textSize = 24f
        }

        fun createDrawable(url: String): Drawable {
            val size = 200
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            val w = textPaint.measureText(url)
            canvas.drawText(url, (size - w) / 2, size / 2f, textPaint)
            return bitmap.toDrawable(
                ApplicationProvider.getApplicationContext<Context>().resources
            )
        }
    }
}