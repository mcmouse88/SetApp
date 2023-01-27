package com.mcmouse88.cats_adapter_espresso

import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.adapter
import com.elveum.elementadapter.addBinding
import com.elveum.elementadapter.context
import com.elveum.elementadapter.setTintColor
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.databinding.ItemCatBinding
import com.mcmouse88.catadapterespresso.databinding.ItemHeaderBinding
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatListItem

interface CatsAdapterListener {
    fun onCatDelete(cat: CatListItem.CatItem)
    fun onCatToggleFavorite(cat: CatListItem.CatItem)
    fun onCatChosen(cat: CatListItem.CatItem)
}

fun catsAdapter(listener: CatsAdapterListener) = adapter {
    addBinding<CatListItem.CatItem, ItemCatBinding> {
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
            ivDelete.onClick { listener.onCatDelete(it) }
            ivFavorite.onClick { listener.onCatToggleFavorite(it) }
            root.onClick { listener.onCatChosen(it) }
        }
    }

    addBinding<CatListItem.Header, ItemHeaderBinding> {
        areItemsSame = { oldHeader, newHeader -> oldHeader.headerId == newHeader.headerId }
        bind { header ->
            tvTitle.text = context().getString(
                R.string.cats,
                header.fromIndex,
                header.toIndex
            )
        }
    }
}