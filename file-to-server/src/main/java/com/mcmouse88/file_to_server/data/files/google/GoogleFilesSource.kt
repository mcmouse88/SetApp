package com.mcmouse88.file_to_server.data.files.google

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.mcmouse88.file_to_server.data.exceptions.SourceExceptionMapper
import com.mcmouse88.file_to_server.data.files.FilesSource
import com.mcmouse88.file_to_server.data.files.google.upload.ReadFileResult
import com.mcmouse88.file_to_server.data.files.google.upload.UploadStrategy
import com.mcmouse88.file_to_server.domain.ReadFileException
import com.mcmouse88.file_to_server.domain.files.RemoteFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleFilesSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleDriveApi: GoogleDriveApi,
    private val sourceExceptionMapper: SourceExceptionMapper,
    private val uploadStrategy: UploadStrategy
) : FilesSource {

    override suspend fun getFiles(): List<RemoteFile> = sourceExceptionMapper.wrap {
        var pageToken: String? = null
        val resultList = ArrayList<RemoteFile>()
        do {
            val response = googleDriveApi.listFiles(pageToken)
            resultList.addAll(response.files.map { it.remoteToFile() })
            pageToken = response.nextPageToken
        } while (pageToken != null)
        return@wrap resultList
    }

    override suspend fun delete(file: RemoteFile) = sourceExceptionMapper.wrap {
        googleDriveApi.delete(file.id)
        return@wrap
    }

    override suspend fun upload(localUri: String) = sourceExceptionMapper.wrap {
        val readFileResult = tryToReadFile(localUri)
        uploadStrategy.upload(readFileResult)
    }

    /**
     * Чтобы получить имя (и другие данные файла) на объекте [contentResolver] нужно вызвать
     * метод [query], который возвращает объект класса [Cursor], далее если курсор не пустой то
     * переходим на первую запись и читаем свойство [DISPLAY_NAME] которое и является именем файла.
     * Для чтения самого файла вызываем на объекте [contentResolver] метод [openInputStream]
     */
    private fun tryToReadFile(localUri: String): ReadFileResult {
        val contentResolver = context.contentResolver
        val androidUri = Uri.parse(localUri)
        try {
            contentResolver.query(
                androidUri,
                null,
                null,
                null,
                null,
            )!!.use { cursor ->
                if (cursor.count == 0) throw IllegalStateException("File not found")
                cursor.moveToFirst()
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val fileName = cursor.getString(nameIndex)
                val bytes = contentResolver.openInputStream(androidUri)!!.use { input ->
                    input.readBytes()
                }
                return ReadFileResult(fileName, bytes)
            }
        } catch (e: Exception) {
            throw ReadFileException(e)
        }
    }
}