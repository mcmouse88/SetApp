package com.mcmouse88.file_to_server.presentation.files

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.file_to_server.data.chooser.FileChooser
import com.mcmouse88.file_to_server.domain.ErrorHandler
import com.mcmouse88.file_to_server.domain.Result
import com.mcmouse88.file_to_server.domain.accounts.AccountRepository
import com.mcmouse88.file_to_server.domain.files.FilesRepository
import com.mcmouse88.file_to_server.domain.files.RemoteFile
import com.mcmouse88.file_to_server.domain.launchIn
import com.mcmouse88.file_to_server.presentation.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FilesViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val filesRepository: FilesRepository,
    private val fileChooser: FileChooser,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData = _stateLiveData.share()

    private val idsInProgressSet = mutableSetOf<String>()
    private val idsInProgressStateFlow = MutableStateFlow(OnChanged())
    private val uploadInProgressStateFlow = MutableStateFlow(false)
    private val reloadInProgressStateFlow = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            val combineFlow = combine(
                filesRepository.getFiles(),
                idsInProgressStateFlow.map { idsInProgressSet },
                uploadInProgressStateFlow,
                reloadInProgressStateFlow,
                ::merge
            )
            combineFlow
                .debounce(100L)
                .collectLatest {
                    _stateLiveData.value = it
                }
        }
    }

    fun load() {
        errorHandler.launchIn(viewModelScope) {
            filesRepository.reload()
        }
    }

    fun reload() {
        errorHandler.launchIn(
            viewModelScope,
            block = {
                reloadInProgressStateFlow.value = true
                filesRepository.reload(silently = true)
            },
            finally = {
                reloadInProgressStateFlow.value = false
            }
        )
    }

    fun delete(item: FileListItem.File) {
        if (idsInProgressSet.contains(item.id)) return
        errorHandler.launchIn(
            viewModelScope,
            block = {
                showInProgress(item)
                filesRepository.delete(item.origin)
            },
            finally = {
                hideInProgress(item)
                idsInProgressStateFlow.value = OnChanged()
            }
        )
    }

    fun signOut() {
        errorHandler.launchIn(viewModelScope) {
            accountRepository.signOut()
        }
    }

    fun chooseFileAndUpload() {
        errorHandler.launchIn(viewModelScope,
            block = {
                val uri = fileChooser.chooseFile()
                uploadInProgressStateFlow.value = true
                filesRepository.upload(uri)
            },
            finally = {
                uploadInProgressStateFlow.value = false
            }
        )
    }

    private fun showInProgress(file: FileListItem.File) {
        idsInProgressSet.add(file.id)
        idsInProgressStateFlow.value = OnChanged()
    }

    private fun hideInProgress(file: FileListItem.File) {
        idsInProgressSet.remove(file.id)
        idsInProgressStateFlow.value = OnChanged()
    }

    private fun merge(
        filesResult: Result<List<RemoteFile>>,
        deleteIdsInProgress: Set<String>,
        isUploadInProgress: Boolean,
        isReloadInProgress: Boolean
    ): State {
        return when (filesResult) {
            is Result.Pending -> State.Loading
            is Result.Error -> State.Error(errorHandler.getErrorMessage(filesResult.exception))
            is Result.Success -> {
                State.Files(
                    files = filesResult.value.map { remoteFile ->
                        FileListItem.File(
                            origin = remoteFile,
                            deleteInProgress = deleteIdsInProgress.contains(remoteFile.id)
                        )
                    },
                    uploadInProgress = isUploadInProgress,
                    reloadInProgress = isReloadInProgress
                )
            }
        }
    }

    sealed interface State {
        object Loading : State
        class Error(val message: String) : State
        data class Files(
            val files: List<FileListItem.File>,
            val uploadInProgress: Boolean,
            val reloadInProgress: Boolean
        ) : State
    }

    private class OnChanged
}