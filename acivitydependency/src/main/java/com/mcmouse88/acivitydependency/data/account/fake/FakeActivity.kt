package com.mcmouse88.acivitydependency.data.account.fake

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.acivitydependency.databinding.ActivityFakeBinding
import com.mcmouse88.acivitydependency.domain.LoginCancelledException
import com.mcmouse88.acivitydependency.domain.LoginFailedException
import com.mcmouse88.acivitydependency.domain.Result
import com.mcmouse88.acivitydependency.domain.accounts.Account

class FakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSuccessSignIn.setOnClickListener {
            val data = Intent().apply {
                putExtra(EXTRA_NAME, "Gandalf is white")
                putExtra(EXTRA_EMAIL, "gandalf.white@email.local")
            }
            setResult(RESULT_OK, data)
            finish()
        }

        binding.btnCancelledSignIn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.btnErrorSignIn.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    class Contract : ActivityResultContract<Unit, Result<Account>>() {

        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, FakeActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Result<Account> {
            if (resultCode == RESULT_OK) {
                val email = intent?.getStringExtra(EXTRA_EMAIL)
                val name = intent?.getStringExtra(EXTRA_NAME)
                if (email != null && name != null) {
                    return Result.Success(Account(email, name))
                }
            } else if (resultCode == RESULT_CANCELED) {
                return Result.Error(LoginCancelledException())
            }
            return Result.Error(LoginFailedException("Internal error", IllegalStateException()))
        }
    }

    private companion object {
        const val EXTRA_EMAIL = "email"
        const val EXTRA_NAME = "name"
    }
}