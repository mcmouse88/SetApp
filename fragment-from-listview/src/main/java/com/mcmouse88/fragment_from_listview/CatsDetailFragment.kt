package com.mcmouse88.fragment_from_listview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.mcmouse88.fragment_from_listview.databinding.FragmentCatsDetailBinding
import com.mcmouse88.fragment_from_listview.model.Cat

class CatsDetailFragment : Fragment() {

    private var _binding: FragmentCatsDetailBinding? = null
    private val binding: FragmentCatsDetailBinding
        get() = _binding ?: throw NullPointerException("FragmentCatsDetailBinding is null")

    private val cat: Cat
        get() = requireArguments().getSerializable(KEY_CAT) as Cat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatsDetailBinding.inflate(inflater, container, false)

        binding.tvName.text = cat.name
        binding.tvDescription.text = cat.description

        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val KEY_CAT = "key_cat"

        fun newInstance(cat: Cat): CatsDetailFragment {
            return CatsDetailFragment().apply {
                arguments = bundleOf(KEY_CAT to cat)
            }
        }
    }
}