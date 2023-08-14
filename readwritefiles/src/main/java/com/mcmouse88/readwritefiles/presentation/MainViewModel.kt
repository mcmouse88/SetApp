package com.mcmouse88.readwritefiles.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.readwritefiles.MutableLiveEvent
import com.mcmouse88.readwritefiles.R
import com.mcmouse88.readwritefiles.model.FilesRepository
import com.mcmouse88.readwritefiles.publishEvent
import com.mcmouse88.readwritefiles.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val filesRepository: FilesRepository
) : ViewModel() {

    private val _contentLiveEvent = MutableLiveEvent<String>()
    val contentLiveEvent = _contentLiveEvent.share()

    private val _errorLiveEvent = MutableLiveEvent<Int>()
    val errorLiveEvent = _errorLiveEvent.share()

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData = _progressLiveData.share()

    fun openFile() = launch {
        val readableFile = filesRepository.openFile()
        showProgress()
        setNewContent(readableFile.read())
    }

    fun saveToFile(content: String) = launch {
        val writableFile = filesRepository.saveFile("my-file.txt")
        showProgress()
        writableFile.write(content)
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block.invoke()
            } catch (e: Exception) {
                if (e !is CancellationException) showError(R.string.cant_access_file)
            }
            hideProgress()
        }
    }

    private fun showError(@StringRes res: Int) {
        _errorLiveEvent.publishEvent(res)
    }

    private fun showProgress() {
        _progressLiveData.value = true
    }

    private fun hideProgress() {
        _progressLiveData.value = false
    }

    private fun setNewContent(content: String) {
        _contentLiveEvent.publishEvent(content)
    }
}