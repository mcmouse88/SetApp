package com.mcmouse88.two_type_adapter.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.*
import com.mcmouse88.two_type_adapter.R
import com.mcmouse88.two_type_adapter.databinding.ActivityMainBinding
import com.mcmouse88.two_type_adapter.databinding.ItemCatBinding
import com.mcmouse88.two_type_adapter.databinding.ItemHeaderBinding
import com.mcmouse88.two_type_adapter.models.Cat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<CatsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        val adapter = createCatsAdapter()
        (binding.rvCats.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.rvCats.adapter = adapter
        viewModel.catsLiveData.observe(this, adapter::submitList)
    }

    private fun createCatsAdapter() = adapter<BaseListItem> {
        addBinding<BaseListItem.CatItem, ItemCatBinding> {
            areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }

            bind { cat ->
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
            }

            listeners {
                ivDelete.onClick(viewModel::deleteCat)
                ivFavorite.onClick(viewModel::toggleFavorite)
                root.onClick { cat ->
                    Toast.makeText(context(), "${cat.name} meow-meow", Toast.LENGTH_SHORT).show()
                }
            }
        }
        addBinding<BaseListItem.HeaderItem, ItemHeaderBinding> {
            areItemsSame = { oldHeader, newHeader -> oldHeader.headerId == newHeader.headerId }
            bind { header ->
                tvTitle.text = getString(R.string.cats, header.fromIndex, header.toIndex)
            }
        }
    }

    // Example of simpleAdapter { ... } usage:
    private fun createOnlyCatAdapter() = simpleAdapter<Cat, ItemCatBinding> {
        areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }
        areContentsSame = { oldCat, newCat -> oldCat == newCat }
        bind { cat ->
            tvCatName.text = cat.name
            tvCatDescription.text = cat.description
        }
        listeners {
            ivDelete.onClick { cat ->
                // delete cat here
            }
            ivFavorite.onClick { cat ->
                // toggle favorite here
            }
            root.onClick { cat ->
                Toast.makeText(context(), "${cat.name} meow-meow", Toast.LENGTH_SHORT).show()
            }
        }
    }
}