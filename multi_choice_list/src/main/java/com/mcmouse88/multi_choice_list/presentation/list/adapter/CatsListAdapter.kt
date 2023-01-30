package com.mcmouse88.multi_choice_list.presentation.list.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import coil.transform.CircleCropTransformation
import com.mcmouse88.multi_choice_list.R
import com.mcmouse88.multi_choice_list.databinding.ItemCatBinding
import com.mcmouse88.multi_choice_list.presentation.list.CatListItem

class CatsListAdapter(
    private val listener: CatsAdapterListener
) : ListAdapter<CatListItem, CatsListAdapter.CatHolder>(CatItemCallback), View.OnClickListener,
    View.OnLongClickListener {

    override fun onClick(v: View) {
        val cat = v.tag as CatListItem
        when (v.id) {
            R.id.deleteImageView -> listener.onCatDelete(cat)
            R.id.favoriteImageView -> listener.onCatToggleFavorite(cat)
            R.id.checkbox -> listener.onCatToggle(cat)
            else -> listener.onCatChosen(cat)
        }
    }

    override fun onLongClick(v: View): Boolean {
        val cat = v.tag as CatListItem
        listener.onCatToggle(cat)
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCatBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)
        binding.deleteImageView.setOnClickListener(this)
        binding.favoriteImageView.setOnClickListener(this)
        binding.checkbox.setOnClickListener(this)

        return CatHolder(binding)
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        val cat = getItem(position)
        with(holder.binding) {
            checkbox.tag = cat
            deleteImageView.tag = cat
            favoriteImageView.tag = cat
            root.tag = cat

            catNameTextView.text = cat.name
            catDescriptionTextView.text = cat.description
            catImageView.load(cat.photoUrl) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.circle)
            }

            favoriteImageView.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    root.context,
                    if (cat.isFavorite) R.color.highlighted_action
                    else R.color.action
                )
            )

            selectionIndicatorView.background = if (cat.isChecked) {
                ColorDrawable(ContextCompat.getColor(root.context, R.color.selected))
            } else {
                null
            }
            checkbox.isChecked = cat.isChecked
        }
    }

    class CatHolder(
        val binding: ItemCatBinding
    ) : ViewHolder(binding.root)

    object CatItemCallback : ItemCallback<CatListItem>() {

        override fun areItemsTheSame(oldItem: CatListItem, newItem: CatListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CatListItem, newItem: CatListItem): Boolean {
            return oldItem == newItem
        }
    }
}