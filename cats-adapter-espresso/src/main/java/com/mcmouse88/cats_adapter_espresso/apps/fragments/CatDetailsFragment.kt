package com.mcmouse88.cats_adapter_espresso.apps.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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
class CatDetailsFragment : Fragment(R.layout.fragment_cat_details), HasTitle {

    @Inject
    lateinit var factory: CatDetailsViewModel.Factory

    @Inject
    lateinit var router: FragmentRouter

    private val viewModel by assistedViewModel {
        val catId = requireArguments().getLong(ARG_CAT_ID)
        factory.create(catId)
    }

    override val title: String
        get() = getString(R.string.fragment_cat_details)

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
                    router.goBack()
                }

                ivFavorite.setOnClickListener {
                    viewModel.toggleFavorite()
                }
            }
        }
    }

    companion object {
        private const val ARG_CAT_ID = "ARG_CAT_ID"

        fun newInstance(catId: Long): CatDetailsFragment {
            return CatDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CAT_ID, catId)
                }
            }
        }
    }
}