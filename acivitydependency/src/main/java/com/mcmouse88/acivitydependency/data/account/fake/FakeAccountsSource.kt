package com.mcmouse88.acivitydependency.data.account.fake

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import com.mcmouse88.acivitydependency.data.ActivityRequired
import com.mcmouse88.acivitydependency.domain.AlreadyInProgressException
import com.mcmouse88.acivitydependency.domain.AuthException
import com.mcmouse88.acivitydependency.domain.CalledNotFromUiException
import com.mcmouse88.acivitydependency.domain.accounts.Account
import com.mcmouse88.acivitydependency.domain.accounts.AccountSource
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject
import com.mcmouse88.acivitydependency.domain.Result

class FakeAccountsSource @Inject constructor() : AccountSource, ActivityRequired {

    private var signInLauncher: ActivityResultLauncher<Unit>? = null
    private var isActivityStarted = false
    private var fakeAccount: Account? = null
    private var completableDeferred: CompletableDeferred<Account>? = null

    override fun onActivityCreated(activity: FragmentActivity) {
        signInLauncher = activity.registerForActivityResult(FakeActivity.Contract()) {
            if (it is Result.Success) {
                completableDeferred?.complete(it.value)
            } else if (it is Result.Error) {
                completableDeferred?.completeExceptionally(it.exception)
            }
            completableDeferred = null
        }
    }

    override fun onActivityStarted() {
        isActivityStarted = true
    }

    override fun onActivityStopped() {
        isActivityStarted = false
    }

    override fun onActivityDestroyed() {
        signInLauncher = null
    }

    override suspend fun oauthSignIn(): Account {
        if (isActivityStarted.not()) throw CalledNotFromUiException()
        val signInLauncher = this.signInLauncher ?: throw CalledNotFromUiException()
        if (completableDeferred != null) throw AlreadyInProgressException()

        signInLauncher.launch(Unit)

        return CompletableDeferred<Account>().let {
            completableDeferred = it
            it.await().also { account ->
                fakeAccount = account
            }
        }
    }

    override suspend fun getAccount(): Account {
        return fakeAccount ?: throw AuthException()
    }

    override suspend fun signOut() {
        fakeAccount = null
    }

    override fun equals(other: Any?): Boolean {
        return other?.javaClass?.name?.equals(javaClass.name) ?: false
    }

    override fun hashCode(): Int {
        return javaClass.name.hashCode()
    }
}