package com.mcmouse88.okhttp.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.databinding.PartResultViewBinding
import com.mcmouse88.okhttp.domain.AuthException
import com.mcmouse88.okhttp.domain.BackendException
import com.mcmouse88.okhttp.domain.ConnectionException
import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.presentation.base.BaseFragment
import com.mcmouse88.okhttp.domain.Error

class ResultView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: PartResultViewBinding
    private var tryAgainAction: (() -> Unit)? = null

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.part_result_view, this, true)
        binding = PartResultViewBinding.bind(this)
    }

    fun setTryAgainAction(action: () -> Unit) {
        this.tryAgainAction = action
    }

    fun<T> setResult(fragment: BaseFragment, result: ResultResponse<T>) {
        binding.tvMessage.isVisible = result is Error<*>
        binding.btnError.isVisible = result is Error<*>
        binding.progressBar.isVisible = result is Pending<*>
        if (result is Error) {
            Log.e(javaClass.simpleName, "Error", result.error)
            val message = when (result.error) {
                is ConnectionException -> context.getString(R.string.connection_error)
                is AuthException -> context.getString(R.string.auth_error)
                is BackendException -> result.error.message
                else -> context.getString(R.string.internal_error)
            }
            binding.tvMessage.text = message
            if (result.error is AuthException) {
                renderLogoutButton(fragment)
            } else {
                renderTryAgainButton()
            }
        }
    }

    private fun renderLogoutButton(fragment: BaseFragment) {
        binding.btnError.setOnClickListener { fragment.logout() }
        binding.btnError.setText(R.string.action_try_again)
    }

    private fun renderTryAgainButton() {
        binding.btnError.setOnClickListener { tryAgainAction?.invoke() }
        binding.btnError.setText(R.string.action_try_again)
    }
}