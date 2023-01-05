package com.mcmouse88.fragmentdialog

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.fragmentdialog.databinding.ActivityDialogLevel1Binding
import com.mcmouse88.fragmentdialog.level_1.*
import kotlin.properties.Delegates.notNull

class DialogLevel1Activity : AppCompatActivity() {

    private var _binding: ActivityDialogLevel1Binding? = null
    private val binding: ActivityDialogLevel1Binding
        get() = _binding ?: throw NullPointerException("ActivityDialogLevel1Binding is null")

    private var color by notNull<Int>()
    private var volume by notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDialogLevel1Binding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.btShowDefaultDialog.setOnClickListener {
            showSimpleDialogFragment()
        }

        binding.btShowSingleChoiceDialog.setOnClickListener {
            showSingleChoiceDialogFragment()
        }

        binding.btShowSingleChoiceDialogWithConfirmation.setOnClickListener {
            showSingleChoiceWithConfirmationDialogFragment()
        }

        binding.btShowMultiplyChoiceDialog.setOnClickListener {
            showMultipleChoiceDialogFragment()
        }

        binding.btShowMultiplyChoiceDialogWithConfirmation.setOnClickListener {
            showMultipleChoiceWithConfirmationDialogFragment()
        }

        volume = savedInstanceState?.getInt(KEY_VOLUME) ?: 50
        color = savedInstanceState?.getInt(KEY_COLOR) ?: Color.RED
        updateUI()

        setupSimpleDialogFragmentListener()
        setupSingleChoiceDialogFragmentListener()
        setupSingleChoiceWithConfirmationDialogFragmentListener()
        setupMultipleChoiceDialogFragmentListener()
        setupMultipleChoiceWithConfirmationDialogFragmentListener()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(KEY_COLOR, color)
        outState.putInt(KEY_VOLUME, volume)
    }

    private fun showSimpleDialogFragment() {
        val dialogFragment = SimpleDialogFragment()
        dialogFragment.show(supportFragmentManager, SimpleDialogFragment.TAG)
    }

    private fun setupSimpleDialogFragmentListener() {
        supportFragmentManager.setFragmentResultListener(
            SimpleDialogFragment.REQUEST_KEY,
            this
        ) { _, result ->
            when(result.getInt(SimpleDialogFragment.KEY_RESPONSE)) {
                DialogInterface.BUTTON_POSITIVE -> showToast(R.string.uninstall_confirmed)
                DialogInterface.BUTTON_NEGATIVE -> showToast(R.string.uninstall_rejected)
                DialogInterface.BUTTON_NEUTRAL -> showToast(R.string.uninstall_ignored)
            }
        }
    }

    private fun showSingleChoiceDialogFragment() {
        SingleChoiceDialogFragment.show(supportFragmentManager, volume)
    }

    private fun setupSingleChoiceDialogFragmentListener() {
        SingleChoiceDialogFragment.setupListener(
            supportFragmentManager,
            this
        ) {
            this.volume = it
            updateUI()
        }
    }

    private fun showSingleChoiceWithConfirmationDialogFragment() {
        SingleChoiceWithConfirmationDialogFragment.show(supportFragmentManager, volume)
    }

    private fun setupSingleChoiceWithConfirmationDialogFragmentListener() {
        SingleChoiceWithConfirmationDialogFragment.setupListener(
            supportFragmentManager,
            this
        ) {
            this.volume = it
            updateUI()
        }
    }

    private fun showMultipleChoiceDialogFragment() {
        MultipleChoiceDialogFragment.show(supportFragmentManager, this.color)
    }

    private fun setupMultipleChoiceDialogFragmentListener() {
        MultipleChoiceDialogFragment.setupListener(supportFragmentManager, this) {
            this.color = it
            updateUI()
        }
    }

    private fun showMultipleChoiceWithConfirmationDialogFragment() {
        MultipleChoiceWithConfirmationDialogFragment.show(supportFragmentManager, this.color)
    }

    private fun setupMultipleChoiceWithConfirmationDialogFragmentListener() {
        MultipleChoiceWithConfirmationDialogFragment.setupListener(
            supportFragmentManager,
            this
        ) {
            this.color = it
            updateUI()
        }
    }

    private fun updateUI() {
        binding.tvCurrentVolume.text = getString(R.string.current_volume, volume)
        binding.colorView.setBackgroundColor(color)
    }

    companion object {
        private val TAG = ActivityDialogLevel1Binding::class.java.simpleName
        private const val KEY_VOLUME = "key_volume"
        private const val KEY_COLOR = "key_color"
    }
}