package com.mcmouse88.result_api

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.result_api.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    /**
     * Все запросы на permission и на запуск новой Активити рекомендуется делать в полях класса
     * Активити, при чем делать их нужно в одном и том же порядке при любых конфигурациях экрана.
     */
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) {
        Log.d(MainActivity::class.java.simpleName, "Permission granted: $it")
        if (it) {
            Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
        }
    }

    private val editMessageLauncher = registerForActivityResult(SecondActivity.Contract()) {
        Log.d(MainActivity::class.java.simpleName, "Edit result: $it")
        if (it != null && it.confirmed) {
            binding.tvHelloWorld.text = it.message
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRequestPermission.setOnClickListener { requestPermission() }
        binding.buttonEdit.setOnClickListener { editMessage() }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun editMessage() {
        editMessageLauncher.launch(binding.tvHelloWorld.text.toString())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}