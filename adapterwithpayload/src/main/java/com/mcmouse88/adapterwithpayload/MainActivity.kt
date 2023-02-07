package com.mcmouse88.adapterwithpayload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.*
import com.mcmouse88.adapterwithpayload.databinding.ActivityMainBinding
import com.mcmouse88.adapterwithpayload.databinding.ItemCatBinding
import com.mcmouse88.adapterwithpayload.databinding.ItemHeaderBinding
import com.mcmouse88.adapterwithpayload.model.Cat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val adapter = createCatsAdapter()
        (binding.rvCat.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.rvCat.layoutManager = LinearLayoutManager(this)
        binding.rvCat.adapter = adapter
        viewModel.catsLiveData.observe(this, adapter::submitList)
    }

    private fun createCatsAdapter() = adapter<CatListItem> {
        addBinding<CatListItem.CatItem, ItemCatBinding> {
            areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
            changePayload = { oldCat, newCat ->
                if (oldCat.isFavorite.not() && newCat.isFavorite) {
                    FAVORITE_FLAG_CHANGED
                } else {
                    NO_ANIMATION
                }
            }

            bindWithPayloads { cat, payloads ->
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
                if (payloads.any { it == FAVORITE_FLAG_CHANGED }) {
                    ivFavorite.startAnimation(animationForFavoriteFlag)
                }
            }

            listeners {
                ivDelete.onClick { viewModel::deleteCat }
                ivFavorite.onClick { viewModel::toggleCat }
                root.onClick { cat ->
                    Toast.makeText(
                        context(),
                        "${cat.name} meow-meows, index: ${index()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            addBinding<CatListItem.Header, ItemHeaderBinding> {
                areItemsSame = { oldItem, newItem -> oldItem.headerId == newItem.headerId }
                bind { header ->
                    tvTitle.text = getString(R.string.cats, header.fromIndex, header.toIndex)
                }
            }
        }
    }

    // Example simple adapter usage
    private fun createOnlyCatsAdapter() = simpleAdapter<Cat, ItemCatBinding> {
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
        areContentsSame = { oldItem, newItem -> oldItem == newItem }

        bind { cat ->
            tvCatName.text = cat.name
            tvCatDescription.text = cat.description
        }
        listeners {
            ivDelete.onClick { cat ->
                // delete cat here
            }

            root.onClick { cat ->
                Toast.makeText(context(), "${cat.name} meow-meows", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val animationForFavoriteFlag by lazy(LazyThreadSafetyMode.NONE) {
        val toSmall = ScaleAnimation(
            1f,
            0.8f,
            1f,
            0.8f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        val smallToLarge = ScaleAnimation(
            1f,
            1.5f,
            1f,
            1.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        val largeToNormal = ScaleAnimation(
            1f,
            0.83f,
            1f,
            0.83f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        val animationSet = AnimationSet(true).apply {
            addAnimation(toSmall)
            addAnimation(smallToLarge)
            addAnimation(largeToNormal)
        }
        animationSet.animations.forEachIndexed { index, animation ->
            animation.duration = 100L
            animation.startOffset = index * 100L
        }
        animationSet
    }

    private companion object {
        val FAVORITE_FLAG_CHANGED = Any()
        val NO_ANIMATION = Any()
    }
}