package com.mcmouse88.user_list.model
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mcmouse88.user_list.R
import com.mcmouse88.user_list.databinding.ItemUserBinding

interface UserActionListener {

    fun onUserMove(user: User, moveBy: Int)

    fun onUserDelete(user: User)

    fun onUserDetail(user: User)

    fun onUserFire(user: User)
}

class UsersAdapter(
    private val userListener: UserActionListener
) : RecyclerView.Adapter<UsersAdapter.AdapterViewHolder>(), View.OnClickListener {

    var users: List<User> = emptyList()

        set(value) {
            val diffCallBack = UserDiffCallBack(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallBack)
            field = value
            diffResult.dispatchUpdatesTo(this)
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

        binding.root.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)

        return AdapterViewHolder(binding)
    }

    /**
     * Если какие-то элементы view списка нужно отрисовать в зависимости от условия, то нужно
     * обязательно это прописывать в ветке if/else, иначе из-за переиспользоввания элементов
     * будут возникать баги.
     */
    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context
        holder.binding.apply {
            holder.itemView.tag = user
            ivMore.tag = user
            tvUserName.text = user.name

            tvUserCompany.text = if (user.company.isNotBlank()) user.company
            else context.getString(R.string.unemployed)
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
        val position = users.indexOfFirst { it.id == user.id }
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

        if (user.company.isNotBlank()) {
            popupMenu.menu.add(
                0,
                ID_FIRE,
                Menu.NONE,
                view.context.getString(R.string.fire)
            )
        }

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> {
                    userListener.onUserMove(user, -1)
                }
                ID_MOVE_DOWN -> {
                    userListener.onUserMove(user, 1)
                }
                ID_REMOVE -> {
                    userListener.onUserDelete(user)
                }
                ID_FIRE -> {
                    userListener.onUserFire(user)
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
        private const val ID_FIRE = 4
    }
}