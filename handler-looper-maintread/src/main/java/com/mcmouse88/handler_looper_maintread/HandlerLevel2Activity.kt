package com.mcmouse88.handler_looper_maintread

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.mcmouse88.handler_looper_maintread.databinding.ActivityHandlerLevelBinding
import kotlin.random.Random

class HandlerLevel2Activity : AppCompatActivity() {

    private var _binding: ActivityHandlerLevelBinding? = null
    private val binding: ActivityHandlerLevelBinding
        get() = _binding ?: throw NullPointerException("ActivityHandlerLevelBinding is null")

    /**
     * В данном примере мы не только передаем [Looper], но и также обрабочик входящих
     * сообщений
     */
    private val handler = Handler(Looper.getMainLooper()) {
        Log.d(TAG, "Processing message: ${it.what}")
        when(it.what) {
            MESSAGE_TOGGLE_BUTTON -> toggleTestButtonState()
            MESSAGE_TEXT_RANDOM_COLOR -> nextRandomColor()
            MESSAGE_SHOW_TOAST -> showToast()
        }
        return@Handler true
    }

    private val token = Any()

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
     * Для того, чтобы передать сообщение создается объект класса [Message] путем вызова
     * метода [obtainMessage()] у объекта класса [Handler]. В качестве параметра в данный
     * метод передается аргумент [what] типа [Int], которые задаются самостоятельно. При передаче
     * сообщения мы только создаем само сообщение с необходимыми нам паарметрами, а как
     * оно будет обрабатываться мы уже описываем внутри обработчика - объекта класса [Handler].
     * Для передачи сообщения используется метод [sendMessage()]. Также можно создать объект
     * сообщения следующим образом, через класс [Message] с пустым конструктором, а дальше
     * сущности [message.what] присвоим идентификатор сообщения, и также отправим его через
     * метод [sendMessage()]. Также можно создать объект [Message] вызвав у него
     * статический метод [obtain] и переадть в качестве пармаетров объект [Handler] и
     * идентификатор сообщения. Чтобы отправить отложенное сообщение используется метод
     * [sendMessageDelayed()]. Также в самом сообщении можно передавать callBack, в таком
     * случае сообщение не будет обрабатываться в [Handler], а просто будет выполнено.
     * Пример --> [R.id.bt_random_color_delay]. Чтобы использовать токен при передаче сообщения
     * нужно в поле объекта [Message]
     * ```css
     * message.obj = token
     * ```
     * записать его, перед отправкой, отменяется сообщение по токену также через метод
     * [removeCallbacksAndMessages()] вызыванный у объекта класса [Handler]
     */
    private val universalButtonListener = View.OnClickListener {
        Thread {
            when(it.id) {
                R.id.bt_enable_disable -> {
                    val message = handler.obtainMessage(MESSAGE_TOGGLE_BUTTON)
                    handler.sendMessage(message)
                }
                R.id.bt_random_color -> {
                    val message = Message()
                    message.what = MESSAGE_TEXT_RANDOM_COLOR
                    handler.sendMessage(message)
                }

                R.id.bt_enable_disable_delay -> {
                    val message = Message.obtain(handler, MESSAGE_TOGGLE_BUTTON)
                    handler.sendMessageDelayed(message, DELAY)
                }
                R.id.bt_random_color_delay -> {
                    val message = Message.obtain(handler) {
                        Log.d(TAG, "Random color is called via CALLBACK")
                        nextRandomColor()
                    }
                    handler.sendMessageDelayed(message, DELAY)
                }

                R.id.bt_random_color_token -> {
                    val message = handler.obtainMessage(MESSAGE_TEXT_RANDOM_COLOR)
                    message.obj = token
                    handler.sendMessageDelayed(message, DELAY)
                }
                R.id.bt_show_toast -> {
                    val message = handler.obtainMessage(MESSAGE_SHOW_TOAST)
                    message.obj = token
                    handler.sendMessageDelayed(message, DELAY)
                }
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
        private val TAG = HandlerLevel2Activity::class.java.simpleName

        private const val MESSAGE_TOGGLE_BUTTON = 1
        private const val MESSAGE_TEXT_RANDOM_COLOR = 2
        private const val MESSAGE_SHOW_TOAST = 3
    }
}