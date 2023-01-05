package com.mcmouse88.okhttp.presentation.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.domain.AuthException
import com.mcmouse88.okhttp.domain.BackendException
import com.mcmouse88.okhttp.domain.ConnectionException
import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.utils.MutableLiveEvent
import com.mcmouse88.okhttp.utils.MutableUnitLiveEvent
import com.mcmouse88.okhttp.utils.logger.Logger
import com.mcmouse88.okhttp.utils.publishEvent
import com.mcmouse88.okhttp.utils.share
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

    private fun logError(e: Throwable) {
        logger.error(javaClass.simpleName, e)
    }

    fun logout() {
        accountsRepository.logout()
    }

    protected fun showErrorMessage(@StringRes messageRes: Int) =
        _showErrorMessageResEvent.publishEvent(messageRes)
}