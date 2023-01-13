package com.mcmouse88.list_adapter.sample_1

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.mcmouse88.list_adapter.R
import com.mcmouse88.list_adapter.databinding.ItemCatBinding
import com.mcmouse88.list_adapter.model.Cat

class CatsAdapter(
    private val listener: Listener
) : ListAdapter<Cat, CatsAdapter.CatHolder>(CatItemCallback), View.OnClickListener {

    override fun onClick(view: View) {
        val cat = view.tag as Cat
        when (view.id) {
            R.id.iv_delete -> listener.onDeleteCat(cat)
            R.id.iv_favorite -> listener.onToggleFavorite(cat)
            else -> listener.onChooseCat(cat)
        }
    }

    /**
     * Наиболее оптимизированным способом назначать слушателей для отдельных элементов итема
     * является назначение в [onCreateViewHolder], так как при назначении в [onBindViewHolder]
     * слушатель будет назначаться каждый раз при скроле списка, и также назначать адаптер в
     * качестве слушателя.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCatBinding.inflate(inflater, parent, false)

        binding.ivDelete.setOnClickListener(this)
        binding.ivFavorite.setOnClickListener(this)
        binding.root.setOnClickListener(this)

        return CatHolder(binding)
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        val cat = getItem(position)

        with(holder.binding) {
            root.tag = cat
            ivDelete.tag = cat
            ivFavorite.tag = cat

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
            val tintColor = if (cat.isFavorite) R.color.highlighted_action
            else R.color.action
            ivFavorite.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(root.context, tintColor)
            )
        }
    }

    interface Listener {
        fun onChooseCat(cat: Cat)
        fun onToggleFavorite(cat: Cat)
        fun onDeleteCat(cat: Cat)
    }

    class CatHolder(
        val binding: ItemCatBinding
    ) : RecyclerView.ViewHolder(binding.root)

    object CatItemCallback: DiffUtil.ItemCallback<Cat>() {

        override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem == newItem
        }
    }
}