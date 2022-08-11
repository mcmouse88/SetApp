package com.mcmouse88.box_quiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcmouse88.box_quiz.BuildConfig
import com.mcmouse88.box_quiz.R
import com.mcmouse88.box_quiz.contract.HasCustomTitle
import com.mcmouse88.box_quiz.contract.navigator
import com.mcmouse88.box_quiz.databinding.FragmentAboutBinding

/**
 * Реализуем интерфейс [HasCustomTitle], для того, чтобы можно было изменить заголовок toolbar в
 * соответствии с названием фрагмента, при этом сам фрагмент о существовании toolbar ничего знать
 * не будет, также переопределим метод [getTitleRes], который будет возвращать строку с названием
 * фрагмента
 */
class AboutFragment : Fragment(), HasCustomTitle {

    private var _binding: FragmentAboutBinding? = null
    private val binding: FragmentAboutBinding
        get() = _binding ?: throw RuntimeException("FragmentAboutBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        binding.tvVersionCode.text = BuildConfig.VERSION_CODE.toString()
        binding.tvVersionName.text = BuildConfig.VERSION_NAME
        binding.btOk.setOnClickListener { onOkPressed() }

        return binding.root
    }


    override fun getTitleRes() = R.string.about

    private fun onOkPressed() {
        navigator().goBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}