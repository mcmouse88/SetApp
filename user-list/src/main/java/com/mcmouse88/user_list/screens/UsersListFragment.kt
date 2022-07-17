package com.mcmouse88.user_list.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse88.user_list.App
import com.mcmouse88.user_list.databinding.FragmentUsersListBinding
import com.mcmouse88.user_list.model.User
import com.mcmouse88.user_list.model.UserActionListener
import com.mcmouse88.user_list.model.UserService
import com.mcmouse88.user_list.model.UsersAdapter
import com.mcmouse88.user_list.viewmodel.UserListViewModel

class UsersListFragment : Fragment() {

    private var _binding: FragmentUsersListBinding? = null
    private val binding: FragmentUsersListBinding
        get() = _binding ?: throw NullPointerException("FragmentUsersListBinding is null")

    private val userListViewModel by viewModels<UserListViewModel> { factory() }

    private val adapter by lazy {
        UsersAdapter(
            object : UserActionListener {

                override fun onUserMove(user: User, moveBy: Int) {
                    userListViewModel.moveUser(user, moveBy)
                }

                override fun onUserDelete(user: User) {
                    userListViewModel.deleteUser(user)
                }

                override fun onUserDetail(user: User) {
                    navigator().showDetail(user)
                }
            })
    }

    /**
     * Вместо this при подписке на события [LiveData] ViewModel  конструктор слежует передавать
     * [viewLifecycleOwner], так как это жизненный цикл интерфейса фрагмента, а не всего фрагмента,
     * и его жизненный цикл заканчивается когда уничтожаются view во фрагменте (сам фрагмент
     * уничтожается после вызова метода [onDestroy()]), таким образом не будет крашей и багов
     * в случае обращения к view, когда они уже уничтожены.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersListBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvListUsers.layoutManager = layoutManager
        binding.rvListUsers.adapter = adapter

        userListViewModel.users.observe(viewLifecycleOwner) {
            adapter.users = it
        }

        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}