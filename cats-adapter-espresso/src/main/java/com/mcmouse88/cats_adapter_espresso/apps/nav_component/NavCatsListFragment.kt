package com.mcmouse88.cats_adapter_espresso.apps.nav_component

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.databinding.FragmentCatsBinding
import com.mcmouse88.cats_adapter_espresso.CatsAdapterListener
import com.mcmouse88.cats_adapter_espresso.catsAdapter
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatListItem
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavCatsListFragment : Fragment(R.layout.fragment_cats), CatsAdapterListener {

    private val viewModel by viewModels<CatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCatsBinding.bind(view)

        val adapter = catsAdapter(this)
        binding.apply {
            (rvCats.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            rvCats.adapter = adapter
        }

        viewModel.catsLiveData.observe(viewLifecycleOwner, adapter::submitList)
    }

    override fun onCatDelete(cat: CatListItem.CatItem) {
        viewModel.deleteCat(cat)
    }

    override fun onCatToggleFavorite(cat: CatListItem.CatItem) {
        viewModel.toggleCat(cat)
    }

    override fun onCatChosen(cat: CatListItem.CatItem) {
        val direction = NavCatsListFragmentDirections
            .actionNavCatsListFragmentToNavCatDetailsFragment(cat.id)
        findNavController().navigate(direction)
    }
}