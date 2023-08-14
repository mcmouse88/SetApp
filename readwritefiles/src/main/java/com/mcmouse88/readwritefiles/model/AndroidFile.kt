package com.mcmouse88.readwritefiles.model

import android.content.Context
import android.net.Uri
import com.mcmouse88.readwritefiles.CantAccessFileException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AndroidFile(
    private val uri: Uri,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : ReadableFile, WriteableFile {

    /**
     * С помощью свойства [contentResolver] можно получить [InputStream/OutputStream] куда нужно
     * передать uri(Universal resource identifier) - ссылку идентификатор на файл и используем
     * extension метод [use](под капотом try/catch with resources), который внутри себя закрывает
     * по завершению (или в случае ошибки) открытые потоки для чтения/записи данных в файл и
     * освобождает память.
     */
    override suspend fun read(): String = withContext(ioDispatcher) {
        try {
            delay(2_000L)
            context.contentResolver.openInputStream(uri)!!.use {
                String(it.readBytes())
            }
        } catch (e: Exception) {
            throw CantAccessFileException(e)
        }
    }

    override suspend fun write(data: String) = withContext(ioDispatcher) {
        try {
            delay(2_000L)
            context.contentResolver.openOutputStream(uri)!!.use {
                it.write(data.toByteArray())
            }
        } catch (e: Exception) {
            throw CantAccessFileException(e)
        }
    }
}