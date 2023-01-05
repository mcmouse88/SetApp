package com.mcmouse88.handler_looper_maintread

import android.os.*
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.mcmouse88.handler_looper_maintread.databinding.ActivityHandlerLevelBinding
import kotlin.random.Random


class HandlerLevel1Activity : AppCompatActivity() {

    private var _binding: ActivityHandlerLevelBinding? = null
    private val binding: ActivityHandlerLevelBinding
        get() = _binding ?: throw NullPointerException("ActivityHandlerLevelBinding is null")

    /**
     * Объект класса [Handler] привязывается к [Looper], и будет работать только в том потоке, в
     * котором существует данный объект [Looper]. Конструкция [Looper.getMainLooper()] возвращает
     * [Looper] главного потока
     */
    private val handler = Handler(Looper.getMainLooper())

    private val token = Any()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHandlerLevelBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.root.forEach {
            if (it is Button) it.setOnClickListener(universalButtonListener)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun toggleTestButtonState() {
        binding.btTest.isEnabled = !binding.btTest.isEnabled
    }

    private fun nextRandomColor() {
        val randomColor = -Random.nextInt(255 * 255 * 255)
        binding.colorView.setBackgroundColor(randomColor)
    }

    private fun showToast() {
        Toast.makeText(this, R.string.hello, Toast.LENGTH_SHORT).show()
    }

    /**
     * Метод [post()] позволяет выполнить код, который передать внутрь этого метода, в потоке к
     * которому привязан [Handler], таким образом выполнение кода из потока можно передать через
     * [Handler] в другой поток (в данном случае в Main). Метод [postDelayed()] позволяет не только
     * передать выполнение в другой поток, но и указать через какое время начать выполнять эти
     * действия. Также в метод [postDelayed()] можно передать в качестве параметра [token], который
     * является типом [Any](это может быть строка, число, ну и собственно любой тип данных,
      * может использоваться в качестве токена). Используя этот токен, можно легко отменить
     * действия переданные на выполнение, но еще не начавшие их выполнять(все действия помеченные
     * токеном будут отменены). Метод [removeCallbacksAndMessages()] с указанием токена в качестве
     * параметра отменяет сообщения направленные на выполнение, которые были переданы с данным
     * токеном. Также можно отменить выполнение и без использования токена, однако это менее
     * удобно. Нужно создать переменную типа [Runnable], которую будем передавать на выполнение
     * ```css
     * val showToastRunnable = Runnable { showToast() }
     * val button = findViewById<Button>(R.id.myButton)
     * button.setOnClickListener { handler.postDelayed(showToastRunnable, 2_000)
     * handler.removeCallbacks(showToastRunnable) --> отмена выполнения
     *```
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private val universalButtonListener = View.OnClickListener {
        Thread {
            when(it.id) {
                R.id.bt_enable_disable -> handler.post { toggleTestButtonState() }
                R.id.bt_random_color -> handler.post { nextRandomColor() }

                R.id.bt_enable_disable_delay ->
                    handler.postDelayed({ toggleTestButtonState() }, DELAY)
                R.id.bt_random_color_delay ->
                    handler.postDelayed({ nextRandomColor() }, DELAY)

                R.id.bt_random_color_token ->
                    handler.postDelayed({ nextRandomColor() }, token, DELAY)
                R.id.bt_show_toast ->
                    handler.postDelayed({ showToast() }, token, DELAY)
                R.id.bt_cancel -> handler.removeCallbacksAndMessages(token)
            }
        }.start()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val DELAY = 2_000L
    }
}