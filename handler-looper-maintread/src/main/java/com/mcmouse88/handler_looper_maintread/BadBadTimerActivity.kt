package com.mcmouse88.handler_looper_maintread

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.handler_looper_maintread.databinding.ActivityBadBadTimerBinding

class BadBadTimerActivity : AppCompatActivity() {

    private var _binding: ActivityBadBadTimerBinding? = null
    private val binding: ActivityBadBadTimerBinding
        get() = _binding ?: throw NullPointerException("ActivityBadBadTimerBinding is null")

    private var thread: Thread? = null
    private var timerValue = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBadBadTimerBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.btStartTimer.setOnClickListener { startTimer() }
        binding.progressBar.max = START_VALUE
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
        _binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Метод [runOnUiThread] указывает на то, что данный код будет выполняться на главном потоке
     * (все действия по обновлению интерфейса должны происходить только в главном потоке), если
     * вызать метод [updateUI] без этого метода, то приложение упадет с ошибкой
     * [ViewRootImplCalledFromWrongThreadException]. Также если вызывать метод [Thread.sleep],
     * либо любые другие методы и действия, которые могут заблоктровать главный поток, то
     * приложение закроется с ошибкой ANR (Application Not Responding)
     */
    private fun startTimer() {
        thread = Thread {
            for (i in START_VALUE downTo 0) {
                timerValue = i
                runOnUiThread{ updateUI() }
                Thread.sleep(1_000)
            }
            runOnUiThread { stopTimer() }
        }
        thread?.start()
    }

    private fun stopTimer() {
        thread = null
        timerValue = START_VALUE
        updateUI()
    }

    private fun updateUI() {
        val timerText = resources.getQuantityString(R.plurals.seconds, timerValue, timerValue)
        binding.tvTimerValue.text = timerText
        binding.progressBar.progress = timerValue
        binding.btStartTimer.isEnabled = thread == null
    }

    companion object {
        private const val START_VALUE = 10
    }
}