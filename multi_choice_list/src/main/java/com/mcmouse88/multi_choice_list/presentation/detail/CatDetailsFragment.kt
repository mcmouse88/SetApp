package com.mcmouse88.multi_choice_list.presentation.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.setTintColor
import com.mcmouse88.multi_choice_list.R
import com.mcmouse88.multi_choice_list.databinding.FragmentCatDetailsBinding
import com.mcmouse88.multi_choice_list.presentation.assistedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CatDetailsFragment : Fragment(R.layout.fragment_cat_details) {

    @Inject
    lateinit var factory: CatDetailsViewModel.Factory

    private val args by navArgs<CatDetailsFragmentArgs>()

    private val viewModel by assistedViewModel {
        factory.create(args.catId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCatDetailsBinding.bind(view)
        viewModel.catsLiveData.observe(viewLifecycleOwner) { cat ->
            binding.catNameTextView.text = cat.name
            binding.catDescriptionTextView.text = cat.description
            binding.catImageView.load(cat.photoUrl) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.circle)
            }

            binding.favoriteImageView.setImageResource(
                if (cat.isFavorite) R.drawable.ic_favorite
                else R.drawable.ic_favorite_not
            )

            binding.favoriteImageView.setTintColor(
                if (cat.isFavorite) R.color.highlighted_action
                else R.color.action
            )
        }

        binding.goBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.favoriteImageView.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }
}