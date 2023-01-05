package com.mcmouse88.box_quiz.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.mcmouse88.box_quiz.Options
import com.mcmouse88.box_quiz.R
import com.mcmouse88.box_quiz.contract.navigator
import com.mcmouse88.box_quiz.databinding.FragmentBoxSelectionBinding
import com.mcmouse88.box_quiz.databinding.ItemBoxBinding
import kotlin.math.max
import kotlin.properties.Delegates
import kotlin.random.Random

class BoxSelectionFragment : Fragment() {

    private var _binding: FragmentBoxSelectionBinding? = null
    private val binding: FragmentBoxSelectionBinding
        get() = _binding ?: throw RuntimeException("FragmentBoxSelectionBinding")

    private lateinit var options: Options

    private var timeStampTimer by Delegates.notNull<Long>()
    private var boxIndex by Delegates.notNull<Int>()
    private var alreadyDone = false

    private var timeHandler: TimerHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        options = arguments?.getParcelable(ARGS_OPTIONS)
            ?: throw IllegalArgumentException("Can't launch BoxSelectionActivity without options")
        boxIndex = savedInstanceState?.getInt(KEY_INDEX) ?: Random.nextInt(options.boxCount)

        timeHandler = if (options.isTimerEnabled) {
            TimerHandler()
        } else {
            null
        }
        timeHandler?.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoxSelectionBinding.inflate(inflater, container, false)
        createBox()
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, boxIndex)
        timeHandler?.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        timeHandler?.onStart()
    }

    override fun onStop() {
        super.onStop()
        timeHandler?.onStop()
    }

    private fun createBox() {
        val boxBinding = (0 until options.boxCount).map {
            val bindingBox = ItemBoxBinding.inflate(layoutInflater)
            bindingBox.root.id = View.generateViewId()
            bindingBox.tvBoxTitle.text = getString(R.string.box_title, it + 1)
            bindingBox.root.setOnClickListener { view -> onBoxSelected(view) }
            bindingBox.root.tag = it
            binding.root.addView(bindingBox.root)
            bindingBox
        }
        binding.boxFlow.referencedIds = boxBinding.map { it.root.id }.toIntArray()
    }

    private fun onBoxSelected(view: View) {
        if (view.tag as Int == boxIndex) {
            alreadyDone = true
            navigator().showBoxScreen()
        } else {
            Toast.makeText(context, R.string.empty_box, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateTimerUI() {
        if (getRemainingSecond() >= 0) {
            binding.tvTimerBox.visibility = View.VISIBLE
            binding.tvTimerBox.text = getString(R.string.timer_value, getRemainingSecond())
        } else {
            binding.tvTimerBox.visibility = View.GONE
        }
    }

    private fun getRemainingSecond(): Long {
        val finishAt = timeStampTimer + TIMER_DURATION
        return max(0, (finishAt - System.currentTimeMillis()) / 1_000)
    }

    private fun showTimerEndDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.the_end))
            .setMessage(R.string.timer_end_message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { _, _ -> navigator().goBack() }
            .create()
        dialog.show()
    }

    inner class TimerHandler {
        private lateinit var timer: CountDownTimer

        fun onCreate(savedInstanceState: Bundle?) {
            timeStampTimer = savedInstanceState?.getLong(KEY_START_TIMESTAMP)
                ?: System.currentTimeMillis()
            alreadyDone = savedInstanceState?.getBoolean(KEY_ALREADY_DONE) ?: false
        }

        fun onSaveInstanceState(outState: Bundle) {
            outState.putLong(KEY_START_TIMESTAMP, timeStampTimer)
            outState.putBoolean(KEY_ALREADY_DONE, alreadyDone)
        }

        fun onStart() {
            if (alreadyDone) return

            timer = object : CountDownTimer(getRemainingSecond() * 1_000, 1_000) {
                override fun onTick(p0: Long) {
                    updateTimerUI()
                }

                override fun onFinish() {
                    updateTimerUI()
                    showTimerEndDialog()
                }
            }
            updateTimerUI()
            timer.start()
        }

        fun onStop() {
            timer.cancel()
        }
    }

    companion object {
        const val ARGS_OPTIONS = "extra_options"
        private const val KEY_INDEX = "key_index"
        private const val KEY_START_TIMESTAMP = "key_start_timestamp"
        private const val KEY_ALREADY_DONE = "key_already_done"
        private const val TIMER_DURATION = 10_000L

        fun createArgs(options: Options) = bundleOf(ARGS_OPTIONS to options)
    }
}