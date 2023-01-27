package com.mcmouse88.cats_adapter_espresso.apps.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.databinding.FragmentCatsBinding
import com.mcmouse88.cats_adapter_espresso.CatsAdapterListener
import com.mcmouse88.cats_adapter_espresso.catsAdapter
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatListItem
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CatsListFragment : Fragment(R.layout.fragment_cats), CatsAdapterListener, HasTitle {

    @Inject
    lateinit var router: FragmentRouter

    private val viewModel by viewModels<CatsViewModel>()

    override val title: String
        get() = getString(R.string.fragment_cats_title)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCatsBinding.bind(view)

        val adapter = catsAdapter(this)
        (binding.rvCats.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.rvCats.adapter = adapter
        viewModel.catsLiveData.observe(viewLifecycleOwner, adapter::submitList)
    }

    override fun onCatDelete(cat: CatListItem.CatItem) {
        viewModel.deleteCat(cat)
    }

    override fun onCatToggleFavorite(cat: CatListItem.CatItem) {
        viewModel.toggleCat(cat)
    }

    override fun onCatChosen(cat: CatListItem.CatItem) {
        router.showDetail(cat.id)
    }
}