package com.mcmouse88.fragmentdialog

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.fragmentdialog.databinding.ActivityDialogLevel2Binding
import com.mcmouse88.fragmentdialog.level_2.CustomDialogFragment
import com.mcmouse88.fragmentdialog.level_2.CustomInputDialogFragment
import com.mcmouse88.fragmentdialog.level_2.CustomInputDialogListener
import com.mcmouse88.fragmentdialog.level_2.CustomSingleChoiceDialogFragment
import kotlin.properties.Delegates

class DialogLevel2Activity : AppCompatActivity() {

    private var _binding: ActivityDialogLevel2Binding? = null
    private val binding: ActivityDialogLevel2Binding
        get() = _binding ?: throw NullPointerException("ActivityDialogLevel2Binding is null")

    private var firstVolume by Delegates.notNull<Int>()
    private var secondVolume by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDialogLevel2Binding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.btShowCustomDialog.setOnClickListener {
            showCustomDialogFragment()
        }

        binding.btShowCustomSingleChoiceDialog.setOnClickListener {
            showCustomSingleChoiceAlertDialog()
        }

        binding.btShowInputDialogOne.setOnClickListener {
            showCustomInputDialogFragment(KEY_FIRST_REQUEST_KEY, firstVolume)
        }

        binding.btShowInputDialogTwo.setOnClickListener {
            showCustomInputDialogFragment(KEY_SECOND_REQUEST_KEY, secondVolume)
        }

        firstVolume = savedInstanceState?.getInt(KEY_FIRST_REQUEST_KEY) ?: 50
        secondVolume = savedInstanceState?.getInt(KEY_SECOND_REQUEST_KEY) ?: 50
        updateUI()

        setupCustomDialogFragmentListener()
        setupCustomSingleChoiceDialogFragmentListener()
        setupCustomInputDialogFragmentListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_VOLUME_FIRST, firstVolume)
        outState.putInt(KEY_VOLUME_SECOND, secondVolume)
    }

    private fun showCustomDialogFragment() {
        CustomDialogFragment.show(supportFragmentManager, firstVolume)
    }

    private fun setupCustomDialogFragmentListener() {
        CustomDialogFragment.setupListener(supportFragmentManager, this) {
            this.firstVolume = it
            updateUI()
        }
    }

    private fun showCustomSingleChoiceAlertDialog() {
        CustomSingleChoiceDialogFragment.show(supportFragmentManager, firstVolume)
    }

    private fun setupCustomSingleChoiceDialogFragmentListener() {
        CustomSingleChoiceDialogFragment.setupListener(supportFragmentManager, this) {
            this.firstVolume = it
            updateUI()
        }
    }

    private fun showCustomInputDialogFragment(requestKey: String, volume: Int) {
        CustomInputDialogFragment.show(supportFragmentManager, volume, requestKey)
    }

    private fun setupCustomInputDialogFragmentListeners() {
        val listener: CustomInputDialogListener = { requestKey, volume ->
            when(requestKey) {
                KEY_FIRST_REQUEST_KEY -> this.firstVolume = volume
                KEY_SECOND_REQUEST_KEY -> this.secondVolume = volume
            }
            updateUI()
        }
        CustomInputDialogFragment.setupListener(
            supportFragmentManager,
            this,
            KEY_FIRST_REQUEST_KEY,
            listener
        )

        CustomInputDialogFragment.setupListener(
            supportFragmentManager,
            this,
            KEY_SECOND_REQUEST_KEY,
            listener
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateUI() {
        binding.tvCurrentVolumeLevelTwoOne.text = getString(R.string.current_volume_1, firstVolume)
        binding.tvCurrentVolumeLevelTwoTwo.text = getString(R.string.current_volume_2, secondVolume)
    }

    companion object {
        private const val KEY_VOLUME_FIRST = "key_volume_first"
        private const val KEY_VOLUME_SECOND = "key_volume_second"

       private const val KEY_FIRST_REQUEST_KEY = "KEY_VOLUME_FIRST_REQUEST_KEY"
       private const val KEY_SECOND_REQUEST_KEY = "KEY_VOLUME_SECOND_REQUEST_KEY"
    }
}