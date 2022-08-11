package com.mcmouse88.box_quiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcmouse88.box_quiz.R
import com.mcmouse88.box_quiz.contract.HasCustomTitle
import com.mcmouse88.box_quiz.contract.navigator
import com.mcmouse88.box_quiz.databinding.FragmentBoxBinding

class BoxFragment : Fragment(), HasCustomTitle {

    private var _binding: FragmentBoxBinding? = null
    private val binding: FragmentBoxBinding
        get() = _binding ?: throw RuntimeException("FragmentBoxBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoxBinding.inflate(inflater, container, false)
        binding.btDone.setOnClickListener { onToMainMenuPressed() }
        return binding.root
    }

    private fun onToMainMenuPressed() {
        navigator().goToMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun getTitleRes() = R.string.box
}