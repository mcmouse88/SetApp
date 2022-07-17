package com.mcmouse88.user_list.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mcmouse88.user_list.R
import com.mcmouse88.user_list.databinding.ItemUserBinding
import com.mcmouse88.user_list.viewmodel.UserListItem

interface UserActionListener {

    fun onUserMove(user: User, moveBy: Int)

    fun onUserDelete(user: User)

    fun onUserDetail(user: User)
}

class UsersAdapter(
    private val userListener: UserActionListener
) : RecyclerView.Adapter<UsersAdapter.AdapterViewHolder>(), View.OnClickListener {

    var users: List<UserListItem> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class AdapterViewHolder(
        val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onClick(view: View) {
        val user = view.tag as User
        when(view.id) {
            R.id.iv_more -> {
                showPopupMenu(view)
            }
            else -> {
                userListener.onUserDetail(user)
            }
        }
    }

    /**
     * Параметр [viewType] используется в случае, если макеты элементов списка отличаются друг от
     * друга
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)

        binding.ivMore.setOnClickListener(this)

        return AdapterViewHolder(binding)
    }

    /**
     * Если какие-то элементы view списка нужно отрисовать в зависимости от условия, то нужно
     * обязательно это прописывать в ветке if/else, иначе из-за переиспользоввания элементов
     * будут возникать баги.
     */
    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val userListItem = users[position]
        val user = userListItem.user
        holder.binding.apply {
            holder.itemView.tag = user
            ivMore.tag = user

            if (userListItem.isProgress) {
                ivMore.visibility = View.INVISIBLE
                itemProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                ivMore.visibility = View.VISIBLE
                itemProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@UsersAdapter)
            }

                tvUserName.text = user.name
            tvUserCompany.text = user.company
            if (user.photo.isNotBlank()) {
                Glide.with(ivAvatar.context)
                    .load(user.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_24)
                    .error(R.drawable.ic_person_24)
                    .into(ivAvatar)
            } else {
                ivAvatar.setImageResource(R.drawable.ic_person_24)
            }
        }
    }

    override fun getItemCount() = users.size

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val user = view.tag as User
        val position = users.indexOfFirst { it.user.id == user.id }
        popupMenu.menu.add(
            0,
            ID_MOVE_UP,
            Menu.NONE,
            view.context.getString(R.string.move_up)
        ).apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(
            0,
            ID_MOVE_DOWN,
            Menu.NONE,
            view.context.getString(R.string.move_down)
        ).apply {
            isEnabled = position < users.size - 1
        }
        popupMenu.menu.add(
            0,
            ID_REMOVE,
            Menu.NONE,
            view.context.getString(R.string.remove)
        )

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                ID_MOVE_UP -> {
                    userListener.onUserMove(user, -1)
                }
                ID_MOVE_DOWN -> {
                    userListener.onUserMove(user, 1)
                }
                ID_REMOVE -> {
                    userListener.onUserDelete(user)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }
}