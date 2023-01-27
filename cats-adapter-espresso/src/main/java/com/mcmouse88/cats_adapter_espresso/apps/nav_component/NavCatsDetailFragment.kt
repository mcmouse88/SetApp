package com.mcmouse88.cats_adapter_espresso.apps.nav_component

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.setTintColor
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.databinding.FragmentCatDetailsBinding
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatDetailsViewModel
import com.mcmouse88.cats_adapter_espresso.viewmodel.base.assistedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NavCatsDetailFragment : Fragment(R.layout.fragment_cat_details) {

    @Inject
    lateinit var factory: CatDetailsViewModel.Factory

    private val viewModel by assistedViewModel {
        val args = NavCatsDetailFragmentArgs.fromBundle(requireArguments())
        factory.create(args.catId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCatDetailsBinding.bind(view)

        viewModel.catLiveData.observe(viewLifecycleOwner) { cat ->
            binding.apply {
                tvCatName.text = cat.name
                tvCatDescription.text = cat.description
                ivCat.load(cat.photoUrl) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.circle)
                }

                ivFavorite.setImageResource(
                    if (cat.isFavorite) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_not
                )

                ivFavorite.setTintColor(
                    if (cat.isFavorite) R.color.highlighted_action
                    else R.color.action
                )

                btnGoBack.setOnClickListener {
                    findNavController().navigateUp()
                }

                ivFavorite.setOnClickListener {
                    viewModel.toggleFavorite()
                }
            }
        }
    }
}