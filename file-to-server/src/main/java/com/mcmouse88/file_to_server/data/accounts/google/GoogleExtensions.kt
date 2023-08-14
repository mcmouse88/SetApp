package com.mcmouse88.file_to_server.data.accounts.google

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.WorkerThread
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.mcmouse88.file_to_server.domain.AuthException
import com.mcmouse88.file_to_server.domain.LoginFailedException
import com.mcmouse88.file_to_server.domain.OperationCancelledException
import com.mcmouse88.file_to_server.domain.Result
import com.mcmouse88.file_to_server.domain.accounts.Account
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val driveFilesScope = "https://www.googleapis.com/auth/drive.file"
private const val profileScope = "https://www.googleapis.com/auth/userinfo.profile"
private const val userScope = "https://www.googleapis.com/auth/userinfo.email"

suspend fun <T> Task<T>.suspend() = suspendCoroutine<T> { continuation ->
    addOnSuccessListener(continuation::resume)
    addOnFailureListener(continuation::resumeWithException)
}

fun GoogleSignInAccount.toInAppAccount(): Account = Account(
    email = email ?: "-",
    displayName = displayName ?: "-"
)

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(
            Scope(driveFilesScope),
            Scope(profileScope),
            Scope(userScope)
        )
        .build()
    return GoogleSignIn.getClient(context, options)
}

fun getGoogleLastSignedInAccount(context: Context): Account {
    return GoogleSignIn.getLastSignedInAccount(context)?.toInAppAccount() ?: throw AuthException()
}

@WorkerThread
fun getGoogleAccessToken(context: Context): String? {
    val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context) ?: return null
    val androidAccount = googleSignInAccount.account ?: return null
    val scopeList = listOf("oauth2", profileScope, driveFilesScope)
    val scopes = scopeList.joinToString(" ")

    return GoogleAuthUtil.getToken(
        context,
        androidAccount,
        scopes
    )
}

class GoogleSignInContract : ActivityResultContract<Unit, Result<Account>>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        val client = getGoogleSignInClient(context)
        return client.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result<Account> {
        return try {
            val account = GoogleSignIn
                .getSignedInAccountFromIntent(intent)
                .getResult(ApiException::class.java)
            Result.Success(account.toInAppAccount())
        } catch (e: ApiException) {
            if (e.statusCode == 12501 || e.status == Status.RESULT_CANCELED) {
                Result.Error(OperationCancelledException())
            } else {
                val message = e.message
                Result.Error(
                    LoginFailedException(
                        message = if (message.isNullOrBlank()) "Internal error!" else message,
                        cause = e
                    )
                )
            }
        }
    }
}
