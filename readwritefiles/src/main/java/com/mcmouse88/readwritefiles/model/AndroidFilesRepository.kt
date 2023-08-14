package com.mcmouse88.readwritefiles.model

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.fragment.app.FragmentActivity
import com.mcmouse88.readwritefiles.ActivityNotStartedException
import com.mcmouse88.readwritefiles.ActivityRequired
import com.mcmouse88.readwritefiles.AlreadyInProgressException
import com.mcmouse88.readwritefiles.CantAccessFileException
import com.mcmouse88.readwritefiles.di.IoDispatcher
import com.mcmouse88.readwritefiles.di.MainDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidFilesRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : FilesRepository, ActivityRequired {

    private var openLauncher: ActivityResultLauncher<Array<String>>? = null
    private var saveLauncher: ActivityResultLauncher<String>? = null

    private var completableDeferred: CompletableDeferred<AndroidFile>? = null
    private var isStarted = false

    override suspend fun openFile(): ReadableFile = withContext(mainDispatcher) {
        assertLauncherState()
        openLauncher?.launch(arrayOf("text/plain"))
        CompletableDeferred<AndroidFile>().let {
            completableDeferred = it
            it.await()
        }
    }

    override suspend fun saveFile(suggestedName: String): WriteableFile =
        withContext(mainDispatcher) {
            assertLauncherState()
            saveLauncher?.launch(suggestedName)
            CompletableDeferred<AndroidFile>().let {
                completableDeferred = it
                it.await()
            }
        }

    /**
     * Уже существующие контракты для открытия файлов [OpenDocument] и создания файлов
     * [CreateDocument], для последнего контракта необходимо указать тип создаваемого файла
     */
    override fun onActivityCreated(activity: FragmentActivity) {
        openLauncher = activity.registerForActivityResult(OpenDocument()) { uri ->
            handleUri(uri)
        }
        saveLauncher = activity.registerForActivityResult(CreateDocument("text/plain")) { uri ->
            handleUri(uri)
        }
    }

    override fun onActivityStarted() {
        isStarted = true
    }

    override fun onActivityStopped() {
        isStarted = false
    }

    override fun onActivityDestroyed(isFinishing: Boolean) {
        openLauncher = null
        saveLauncher = null
        if (isFinishing) {
            completableDeferred?.cancel()
            completableDeferred = null
        }
    }

    private fun handleUri(uri: Uri?) {
        try {
            if (uri == null) {
                completableDeferred?.cancel()
            } else {
                completableDeferred?.complete(AndroidFile(uri, appContext, ioDispatcher))
            }
        } catch (e: Exception) {
            completableDeferred?.completeExceptionally(CantAccessFileException(e))
        }
        completableDeferred = null
    }

    private fun assertLauncherState() {
        if (isStarted.not()) throw ActivityNotStartedException()
        if (completableDeferred != null) throw AlreadyInProgressException()
    }
}