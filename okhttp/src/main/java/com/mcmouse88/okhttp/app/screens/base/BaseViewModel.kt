package com.mcmouse88.okhttp.app.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.app.model.AuthException
import com.mcmouse88.okhttp.app.model.BackendException
import com.mcmouse88.okhttp.app.model.ConnectionException
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.utiils.MutableLiveEvent
import com.mcmouse88.okhttp.app.utiils.MutableUnitLiveEvent
import com.mcmouse88.okhttp.app.utiils.logger.Logger
import com.mcmouse88.okhttp.app.utiils.publishEvent
import com.mcmouse88.okhttp.app.utiils.share
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel(
    private val accountsRepository: AccountsRepository,
    private val logger: Logger
) : ViewModel() {

    private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()

    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()

    private val _showAuthErrorAndRestartEvent = MutableUnitLiveEvent()
    val showAuthErrorAndRestartEvent = _showAuthErrorAndRestartEvent.share()

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                block.invoke(this)
            } catch (e: ConnectionException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.connection_error)
            } catch (e: BackendException) {
                logError(e)
                _showErrorMessageEvent.publishEvent(e.message ?: "")
            } catch (e: AuthException) {
                logError(e)
                _showAuthErrorAndRestartEvent.publishEvent()
            } catch (e: Exception) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.internal_error)
            }
        }
    }

    fun logError(e: Throwable) {
        logger.error(javaClass.simpleName, e)
    }

    fun logout() {
        accountsRepository.logout()
    }
}