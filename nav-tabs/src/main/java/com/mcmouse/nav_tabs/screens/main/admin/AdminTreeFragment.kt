package com.mcmouse.nav_tabs.screens.main.admin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.Repositories
import com.mcmouse.nav_tabs.databinding.FragmentAdminTreeBinding
import com.mcmouse.nav_tabs.utils.resources.ContextResources
import com.mcmouse.nav_tabs.utils.viewModelCreator

class AdminTreeFragment : Fragment(R.layout.fragment_admin_tree) {

    private var _binding: FragmentAdminTreeBinding? = null
    private val binding: FragmentAdminTreeBinding
        get() = _binding ?: throw NullPointerException("FragmentAdminTreeBinding")

    private val viewModel by viewModelCreator {
        AdminViewModel(Repositories.accountsRepository, ContextResources(requireContext()))
    }

    private val adapter: AdminItemsAdapter by lazy {
        AdminItemsAdapter(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminTreeBinding.bind(view)

        val layoutManager = LinearLayoutManager(requireContext())

        binding.rvAdminTree.layoutManager = layoutManager
        binding.rvAdminTree.adapter = adapter

        observeTreeItem()
    }

    private fun observeTreeItem() {
        viewModel.items.observe(viewLifecycleOwner) {
            adapter.renderItems(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}