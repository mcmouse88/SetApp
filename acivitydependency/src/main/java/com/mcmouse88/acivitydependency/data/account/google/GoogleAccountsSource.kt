package com.mcmouse88.acivitydependency.data.account.google

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import com.mcmouse88.acivitydependency.data.ActivityRequired
import com.mcmouse88.acivitydependency.domain.AlreadyInProgressException
import com.mcmouse88.acivitydependency.domain.CalledNotFromUiException
import com.mcmouse88.acivitydependency.domain.InternalException
import com.mcmouse88.acivitydependency.domain.accounts.Account
import com.mcmouse88.acivitydependency.domain.accounts.AccountSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject
import javax.inject.Singleton
import com.mcmouse88.acivitydependency.domain.Result

@Singleton
class GoogleAccountsSource @Inject constructor(
    @ApplicationContext private val context: Context
) : AccountSource, ActivityRequired {

    private var isActivityStarted = false
    private var signInLauncher: ActivityResultLauncher<Unit>? = null
    private var completableDeferred: CompletableDeferred<Account>? = null

    override fun onActivityCreated(activity: FragmentActivity) {
        signInLauncher = activity.registerForActivityResult(GoogleSignInContract()) {
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
            it.await()
        }
    }

    override suspend fun getAccount(): Account {
        return getGoogleLastSignedInAccount(context)
    }

    override suspend fun signOut() {
        try {
            getGoogleSignInClient(context).signOut().suspend()
        } catch (e: Throwable) {
            throw InternalException(e)
        }
    }

    /**
     * методы [equals] и [hashCode] переопределены для корректной работы при работе с Activity
     * Result Api, так как результаты могут придти не в том порядке при смене жизненного цикла
     * активити
     */
    override fun equals(other: Any?): Boolean {
        return other?.javaClass?.name?.equals(javaClass.name) ?: false
    }

    override fun hashCode(): Int {
        return javaClass.name.hashCode()
    }
}