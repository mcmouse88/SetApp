package com.mcmouse88.mvvm_saved_state

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.setMargins
import com.mcmouse88.mvvm_saved_state.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private val viewModel by viewModels<MainViewModel> { MyViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        viewModel.squares.observe(this) {
            renderSquares(it)
        }

        binding.btGenerateColor.setOnClickListener { viewModel.generateSquares() }

    }

    private fun renderSquares(square: Square) = with(binding) {
        colorContainer.removeAllViews()
        val identifiers = square.color.indices.map { View.generateViewId() }
        for (i in square.color.indices) {
            val row = i / square.size
            val column = i % square.size

            val view = View(this@MainActivity)
            view.setBackgroundColor(square.color[i])
            view.id = identifiers[i]

            val params = LayoutParams(0, 0)
            params.setMargins(resources.getDimensionPixelSize(R.dimen.space))
            view.layoutParams = params

            if (column == 0) params.startToStart = LayoutParams.PARENT_ID
            else params.startToEnd = identifiers[i - 1]

            if(column == square.size - 1) params.endToEnd = LayoutParams.PARENT_ID
            else params.endToStart = identifiers[i + 1]

            if (row == 0) params.topToTop = LayoutParams.PARENT_ID
            else params.topToBottom = identifiers[i - square.size]

            if (row == square.size - 1) params.bottomToBottom = LayoutParams.PARENT_ID
            else params.bottomToTop = identifiers[i + square.size]

            colorContainer.addView(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}