package com.mcmouse88.paging_library.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mcmouse88.paging_library.R
import com.mcmouse88.paging_library.databinding.ItemUserBinding
import com.mcmouse88.paging_library.model.users.User
import com.mcmouse88.paging_library.views.UserListItem

/**
 * Если мы хотим, чтобы данные отображались постранично, то нужно адаптер унаследовать от
 * [PagingDataAdapter], сожержащий два параметра, это тип который будем отрисовывать, то есть
 * [User], и [ViewHolder]
 */
class UsersAdapter(
    private val listener: Listener
) : PagingDataAdapter<UserListItem, UsersAdapter.Holder>(UsersDiffCallback()), View.OnClickListener {

    override fun onClick(v: View) {
        val user = v.tag as UserListItem
        if (v.id == R.id.iv_star) {
            listener.onToggleFavoriteFlag(user)
        } else if (v.id == R.id.iv_delete) {
            listener.onUserDelete(user)
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = getItem(position) ?: return
        with(holder.binding) {
            tvUserName.text = user.name
            tvUserCompany.text = user.company

            progressBarUser.alpha = if (user.inProgress) 1f else 0f
            ivStar.isInvisible = user.inProgress
            ivDelete.isInvisible = user.inProgress

            setIsFavorite(ivStar, user.isFavorite)
            loadUserPhoto(ivPhoto, user.imageUrl)

            ivStar.tag = user
            ivDelete.tag = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        binding.ivStar.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
        return Holder((binding))
    }

    private fun loadUserPhoto(imageView: ImageView, url: String) {
        val context = imageView.context
        if (url.isNotBlank()) {
            Glide.with(context)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_user_avatar)
                .error(R.drawable.ic_user_avatar)
                .into(imageView)
        } else {
            Glide.with(context)
                .load(R.drawable.ic_user_avatar)
                .into(imageView)
        }
    }

    private fun setIsFavorite(starImageView: ImageView, isFavorite: Boolean) {
        val context = starImageView.context
        if (isFavorite) {
            starImageView.setImageResource(R.drawable.ic_star)
            starImageView.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.active)
            )
        } else {
            starImageView.setImageResource(R.drawable.ic_star_outline)
            starImageView.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.inactive)
            )
        }
    }

    class Holder(
        val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        fun onUserDelete(userListItem: UserListItem)

        fun onToggleFavoriteFlag(userListItem: UserListItem)
    }
}

class UsersDiffCallback : DiffUtil.ItemCallback<UserListItem>() {

    override fun areItemsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        return oldItem == newItem
    }
}