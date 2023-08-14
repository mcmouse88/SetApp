package com.mcmouse88.file_to_server.domain.files

import com.mcmouse88.file_to_server.data.files.FilesSource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.mcmouse88.file_to_server.domain.Result
import com.mcmouse88.file_to_server.domain.ignoreErrors
import com.mcmouse88.file_to_server.domain.suppressExceptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription

@Singleton
class FilesRepository @Inject constructor(
    private val filesSource: FilesSource
) {

    private val filesFlow = MutableStateFlow<Result<List<RemoteFile>>>(Result.Pending())

    fun getFiles(): Flow<Result<List<RemoteFile>>> {
        return filesFlow
            .onSubscription {
                emit(Result.Pending())
                ignoreErrors { reload(silently = true) }
            }
    }

    suspend fun reload(silently: Boolean = false) {
        try {
            if (silently.not()) filesFlow.value = Result.Pending()
            filesFlow.value = Result.Success(filesSource.getFiles())
        } catch (e: Exception) {
            filesFlow.value = Result.Error(e)
            throw e
        }
    }

    suspend fun delete(file: RemoteFile) {
        filesSource.delete(file)
        suppressExceptions {
            reload(silently = true)
        }
    }

    suspend fun upload(uri: String) {
        filesSource.upload(uri)
        suppressExceptions {
            reload(silently = true)
        }
    }
}