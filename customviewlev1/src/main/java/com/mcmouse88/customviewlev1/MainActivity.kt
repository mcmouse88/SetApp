package com.mcmouse88.customviewlev1

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.customviewlev1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private val handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.bottomButtons.setOnBottomButtonClickListener {
            when (it) {
                BottomButtonAction.POSITIVE -> {
                    binding.bottomButtons.setButtonPositiveText("Updated Ok")

                }
                BottomButtonAction.NEGATIVE -> {
                    binding.bottomButtons.setOnNegativeButtonText("Cancel Nah")
                }
            }
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(TOKEN)
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val TOKEN = "token"
    }
}