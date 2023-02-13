package com.mcmouse88.acivitydependency.data.account.google

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.mcmouse88.acivitydependency.domain.AuthException
import com.mcmouse88.acivitydependency.domain.LoginCancelledException
import com.mcmouse88.acivitydependency.domain.LoginFailedException
import com.mcmouse88.acivitydependency.domain.accounts.Account
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.mcmouse88.acivitydependency.domain.Result

suspend fun <T> Task<T>.suspend() = suspendCoroutine<T> { continuation ->
    addOnSuccessListener {
        continuation.resume(it)
    }
    addOnFailureListener {
        continuation.resumeWithException(it)
    }
}

fun GoogleSignInAccount.toInAppAccount(): Account {
    return Account(
        email = email ?: "-",
        displayName = displayName ?: "-"
    )
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestProfile()
        .build()
    return GoogleSignIn.getClient(context, options)
}

fun getGoogleLastSignedInAccount(context: Context): Account {
    return GoogleSignIn.getLastSignedInAccount(context)
        ?.toInAppAccount()
        ?: throw AuthException()
}

class GoogleSignInContract : ActivityResultContract<Unit, Result<Account>>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        val client = getGoogleSignInClient(context)
        return client.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result<Account> {
        return try {
            val result = GoogleSignIn
                .getSignedInAccountFromIntent(intent)
                .getResult(ApiException::class.java)
            Result.Success(result.toInAppAccount())
        } catch (e: ApiException) {
            if (e.statusCode == 12501 || e.status == Status.RESULT_CANCELED) {
                Result.Error(LoginCancelledException(e))
            } else {
                val message = e.message
                Result.Error(
                    LoginFailedException(
                        if (message == null || message.isBlank()) {
                            "Internal Error"
                        } else {
                            message
                        },
                        e
                    )
                )
            }
        }
    }
}
