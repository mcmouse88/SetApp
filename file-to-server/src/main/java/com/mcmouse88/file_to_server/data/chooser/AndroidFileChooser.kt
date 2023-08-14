package com.mcmouse88.file_to_server.data.chooser

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.mcmouse88.file_to_server.data.ActivityRequired
import com.mcmouse88.file_to_server.domain.AlreadyInProgressException
import com.mcmouse88.file_to_server.domain.CalledNotFromUiException
import com.mcmouse88.file_to_server.domain.OperationCancelledException
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidFileChooser @Inject constructor() : FileChooser, ActivityRequired {

    private var isStarted = false
    private var launcher: ActivityResultLauncher<Array<String>>? = null
    private var completableDeferred: CompletableDeferred<String>? = null

    override fun onActivityCreated(activity: FragmentActivity) {
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri == null) {
                completableDeferred?.completeExceptionally(OperationCancelledException())
            } else {
                completableDeferred?.complete(uri.toString())
            }
            completableDeferred = null
        }
    }

    override fun onActivityStarted() {
        isStarted = true
    }

    override fun onActivityStopped() {
        isStarted = false
    }

    override fun onActivityDestroyed() {
        launcher = null
    }

    override suspend fun chooseFile(): String {
        if (isStarted.not()) throw CalledNotFromUiException()
        val launcher = this.launcher ?: throw CalledNotFromUiException()
        if (completableDeferred != null) throw AlreadyInProgressException()

        val completableDeferred = CompletableDeferred<String>()
        this.completableDeferred = completableDeferred
        launcher.launch(arrayOf("*/*"))
        return completableDeferred.await()
    }
}