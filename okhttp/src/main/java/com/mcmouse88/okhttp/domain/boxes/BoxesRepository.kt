package com.mcmouse88.okhttp.domain.boxes

import com.mcmouse88.okhttp.domain.Empty
import com.mcmouse88.okhttp.domain.Error
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.domain.accounts.entities.Account
import com.mcmouse88.okhttp.domain.boxes.entities.Box
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.domain.wrapBackendException
import com.mcmouse88.okhttp.utils.async.LazyFlowSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoxesRepository @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val boxesSource: BoxesSource
) {

    private var accountResult: ResultResponse<Account> = Empty()

    private val boxesLazyFlowSubject = LazyFlowSubject<BoxesFilter, List<BoxAndSettings>> { filter ->
        wrapBackendException { boxesSource.getBoxes(filter) }
    }

    fun getBoxesAndSettings(filter: BoxesFilter): Flow<ResultResponse<List<BoxAndSettings>>> {
        return accountsRepository.getAccount()
            .onEach {
                accountResult = it
            }.flatMapLatest { result ->
                if (result is Success) boxesLazyFlowSubject.listen(filter)
                else flowOf(result.mapResult())
            }
    }

    fun reload(filter: BoxesFilter) {
        if (accountResult is Error) accountsRepository.reloadAccount()
        else boxesLazyFlowSubject.reloadArgument(filter)
    }

    suspend fun activateBox(box: Box) = wrapBackendException {
        boxesSource.setIsActive(box.id, true)
        boxesLazyFlowSubject.reloadAll(silentMode = true)
    }

    suspend fun deactivateBox(box: Box) = wrapBackendException {
        boxesSource.setIsActive(box.id, false)
        boxesLazyFlowSubject.reloadAll(silentMode = true)
    }
}