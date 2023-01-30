package com.mcmouse88.multi_choice_list.presentation.list.adapter

import android.graphics.drawable.ColorDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.getColor
import com.elveum.elementadapter.setTintColor
import com.elveum.elementadapter.simpleAdapter
import com.mcmouse88.multi_choice_list.R
import com.mcmouse88.multi_choice_list.databinding.ItemCatBinding
import com.mcmouse88.multi_choice_list.presentation.list.CatListItem

fun catsSimpleAdapter(listener: CatsAdapterListener) = simpleAdapter<CatListItem, ItemCatBinding> {
    areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }

    bind { cat ->
        catNameTextView.text = cat.name
        catDescriptionTextView.text = cat.description
        catImageView.load(cat.photoUrl) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.circle)
        }

        favoriteImageView.setImageResource(
            if (cat.isFavorite) R.drawable.ic_favorite
            else R.drawable.ic_favorite_not
        )

        favoriteImageView.setTintColor(
            if (cat.isFavorite) R.color.highlighted_action
            else R.color.action
        )

        selectionIndicatorView.background = if (cat.isChecked) {
            ColorDrawable(getColor(R.color.selected))
        } else {
            null
        }
        checkbox.isChecked = cat.isChecked
    }

    listeners {
        deleteImageView.onClick(listener::onCatDelete)
        favoriteImageView.onClick(listener::onCatToggleFavorite)
        checkbox.onClick(listener::onCatToggle)
        root.onClick(listener::onCatChosen)
        root.onLongClick {
            listener.onCatToggle(it)
            true
        }
    }
}