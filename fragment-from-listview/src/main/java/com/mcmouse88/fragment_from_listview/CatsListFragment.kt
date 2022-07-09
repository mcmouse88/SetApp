package com.mcmouse88.fragment_from_listview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.mcmouse88.fragment_from_listview.databinding.FragmentCatsListBinding

class CatsListFragment : Fragment() {

    private var _binding: FragmentCatsListBinding? = null
    private val binding: FragmentCatsListBinding
        get() = _binding ?: throw NullPointerException("FragmentCatsListBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatsListBinding.inflate(inflater, container, false)

        val cats = contract().carsService.cats
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cats)
        binding.catsView.adapter = adapter
        binding.catsView.setOnItemClickListener { _, _, position, _ ->
            val currentCat = adapter.getItem(position)!!
            contract().launchCatDetailScreen(currentCat)
        }
        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}