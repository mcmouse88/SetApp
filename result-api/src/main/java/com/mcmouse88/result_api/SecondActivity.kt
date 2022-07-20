package com.mcmouse88.result_api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.result_api.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private var _binding: ActivitySecondBinding? = null
    private val binding: ActivitySecondBinding
        get() = _binding ?: throw NullPointerException("ActivitySecondBinding is null")

    private val resultIntent: Intent
        get() = Intent().apply {
            putExtra(EXTRA_OUTPUT_MESSAGE, binding.etEnterValue.text.toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySecondBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.buttonCancel.setOnClickListener { onBackPressed() }
        binding.buttonSave.setOnClickListener { onSavedPressed() }

        binding.etEnterValue.setText(intent.getStringExtra(EXTRA_INPUT_MESSAGE))
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, resultIntent)
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun onSavedPressed() {
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    data class Output(
        val message: String,
        val confirmed: Boolean
    )

    /**
     * для того, чтобы пеердать данные в активити, и получить результат обратно, нужно создать
     * специальный класс, который наследуется от класса [ActivityResultContract] в diamond
     * оператор нужно передать два параметра, входящийй на следующую активити, и исходящий,
     * который вернется в качестве результата.
     */
    class Contract : ActivityResultContract<String, Output>() {

        /**
         * Метод [createIntent()] используется для того, чтобы запустить вторую Активити, внутри
         * этого метода мы создаем [Intent], который будем передавать на вторую Активити
         */
        override fun createIntent(context: Context, input: String?): Intent =
            Intent(context, SecondActivity::class.java).apply {
                putExtra(EXTRA_INPUT_MESSAGE, input)
            }

        /**
         * Метод [parseResult()] обрабатывает результат приходящий на первую Активити
         */
        override fun parseResult(resultCode: Int, intent: Intent?): Output? {
            if (intent == null) return null
            val message = intent.getStringExtra(EXTRA_OUTPUT_MESSAGE) ?: return null

            val confirmed = resultCode == RESULT_OK
            return Output(message, confirmed)
        }

        /**
         * Если при выполнении определенного условия, нам не нужно переходить на другое Активити,
         * а сразу получить результат, то нужно переопределить метод [getSynchronousResult()],
         * в котором прописать условия. Для примера мы проверяем, если в TextView в качестве
         * строки записано число, то мы его будем инкрементировать на 1, без перехода
         * на другое Активити
         */
        override fun getSynchronousResult(
            context: Context,
            input: String?
        ): SynchronousResult<Output>? {
            val number = input?.toIntOrNull()
            return if (number != null) {
                val incrementedNumber = number + 1
                SynchronousResult(Output(incrementedNumber.toString(), true))
            } else {
                null
            }
        }
    }

    companion object {
        private const val EXTRA_INPUT_MESSAGE = "extra_message"
        private const val EXTRA_OUTPUT_MESSAGE = "extra_message"
    }
}