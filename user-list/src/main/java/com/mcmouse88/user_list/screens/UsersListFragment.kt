package com.mcmouse88.user_list.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse88.user_list.databinding.FragmentUsersListBinding
import com.mcmouse88.user_list.model.UsersAdapter
import com.mcmouse88.user_list.screens.tasks.EmptyResult
import com.mcmouse88.user_list.screens.tasks.ErrorResult
import com.mcmouse88.user_list.screens.tasks.PendingResult
import com.mcmouse88.user_list.screens.tasks.SuccessResult
import com.mcmouse88.user_list.viewmodel.UserListViewModel

class UsersListFragment : Fragment() {

    private var _binding: FragmentUsersListBinding? = null
    private val binding: FragmentUsersListBinding
        get() = _binding ?: throw NullPointerException("FragmentUsersListBinding is null")

    private val userListViewModel by viewModels<UserListViewModel> { factory() }

    private val adapter by lazy {
        UsersAdapter(userListViewModel)
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

        userListViewModel.users.observe(viewLifecycleOwner) {
            hideItemView()
            when(it) {
                is SuccessResult -> {
                    binding.rvListUsers.visibility = View.VISIBLE
                    adapter.users = it.data
                }
                is ErrorResult -> {
                    binding.containerTryAgain.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.tvNoUsers.visibility = View.VISIBLE
                }
            }
        }

        userListViewModel.actionShowDetails.observe(viewLifecycleOwner) {
            it.getValue()?.let { user -> navigator().showDetail(user) }
        }

        userListViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> navigator().toast(messageRes) }
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvListUsers.layoutManager = layoutManager
        binding.rvListUsers.adapter = adapter
        return binding.root
    }

    private fun hideItemView() {
        binding.rvListUsers.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.containerTryAgain.visibility = View.GONE
        binding.tvNoUsers.visibility = View.GONE
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}