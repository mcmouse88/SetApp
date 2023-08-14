package com.mcmouse88.readwritefiles.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mcmouse88.readwritefiles.ActivityRequired
import com.mcmouse88.readwritefiles.databinding.ActivityMainBinding
import com.mcmouse88.readwritefiles.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var activityRequiredSet: Set<@JvmSuppressWildcards ActivityRequired>

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.apply {
            bindListeners()
            observeViewModel()
        }

        activityRequiredSet.forEach { it.onActivityCreated(this) }
    }

    private fun ActivityMainBinding.bindListeners() {
        btnOpen.setOnClickListener {
            viewModel.openFile()
        }
        btnSave.setOnClickListener {
            viewModel.saveToFile(etContent.text.toString())
        }
    }

    private fun ActivityMainBinding.observeViewModel() {
        val mainActivity = this@MainActivity
        viewModel.contentLiveEvent.observeEvent(mainActivity) {
            etContent.setText(it)
        }
        viewModel.errorLiveEvent.observeEvent(mainActivity) {
            Toast.makeText(mainActivity, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.progressLiveData.observe(mainActivity) {
            etContent.isEnabled = it.not()
            btnSave.isEnabled = it.not()
            btnOpen.isEnabled = it.not()
            progressContainer.isVisible = it
        }
    }

    override fun onStart() {
        super.onStart()
        activityRequiredSet.forEach { it.onActivityStarted() }
    }

    override fun onStop() {
        super.onStop()
        activityRequiredSet.forEach { it.onActivityStopped() }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityRequiredSet.forEach { it.onActivityDestroyed(isFinishing) }
    }
}