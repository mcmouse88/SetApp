package com.mcmouse88.fragmentdialog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.fragmentdialog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.btLevelOne.setOnClickListener {
            startActivity(Intent(this, DialogLevel1Activity::class.java))
        }

        binding.btLevelTwo.setOnClickListener {
            startActivity(Intent(this, DialogLevel2Activity::class.java))
        }

        binding.btExit.setOnClickListener { finish() }

    }
}