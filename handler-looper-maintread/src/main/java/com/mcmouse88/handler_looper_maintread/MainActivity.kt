package com.mcmouse88.handler_looper_maintread

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.handler_looper_maintread.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.buttonCrash.setOnClickListener {
            startActivity(Intent(this, BadBadTimerActivity::class.java))
        }

        binding.btHandlerLev1.setOnClickListener {
            startActivity(Intent(this, HandlerLevel1Activity::class.java))
        }

        binding.btHandlerLev2.setOnClickListener {
            startActivity(Intent(this, HandlerLevel2Activity::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}