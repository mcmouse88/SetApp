package com.mcmouse88.cats_adapter_espresso.apps.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
class CatDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: CatDetailsViewModel.Factory

    private val viewModel by assistedViewModel {
        val catId = intent.getLongExtra(EXTRA_CAT_ID, -1)
        factory.create(catId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            FragmentCatDetailsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        viewModel.catLiveData.observe(this) { cat ->
            binding.apply {
                tvCatName.text = cat.name
                tvCatDescription.text = cat.description
                binding.ivCat.load(cat.photoUrl) {
                    transformations(CircleCropTransformation())
                        .placeholder(R.drawable.circle)
                }
                ivFavorite.setImageResource(
                    if (cat.isFavorite) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_not
                )
                ivFavorite.setTintColor(
                    if (cat.isFavorite) R.color.highlighted_action
                    else R.color.action
                )

                btnGoBack.setOnClickListener { finish() }
                ivFavorite.setOnClickListener { viewModel.toggleFavorite() }
            }
        }
    }

    companion object {
        const val EXTRA_CAT_ID = "EXTRA_CAT_ID"
    }
}