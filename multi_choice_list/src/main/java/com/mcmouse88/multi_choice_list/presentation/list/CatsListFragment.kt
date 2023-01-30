package com.mcmouse88.multi_choice_list.presentation.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse88.multi_choice_list.R
import com.mcmouse88.multi_choice_list.databinding.FragmentCatsBinding
import com.mcmouse88.multi_choice_list.presentation.base.CustomToolbarAction
import com.mcmouse88.multi_choice_list.presentation.base.ToolbarAction
import com.mcmouse88.multi_choice_list.presentation.base.ToolbarUpdater
import com.mcmouse88.multi_choice_list.presentation.list.adapter.CatsAdapterListener
import com.mcmouse88.multi_choice_list.presentation.list.adapter.catsSimpleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatsListFragment : Fragment(R.layout.fragment_cats), CatsAdapterListener, CustomToolbarAction {

    private val viewModel by viewModels<CatsViewModel>()

    private var toolbarUpdater: ToolbarUpdater? = null

    override val action: ToolbarAction?
        get() {
            val state = viewModel.stateLiveData.value ?: return null
            if (state.totalCheckedCount > 0) {
                return ToolbarAction(
                    iconRes = R.drawable.ic_delete,
                    action = { viewModel.deleteSelectedCats() }
                )
            }
            return null
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCatsBinding.bind(view)
        val adapter = catsSimpleAdapter(this)
        binding.catsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        (binding.catsRecyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.catsRecyclerView.adapter = adapter

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.cats)
            binding.selectOrClearAllTextView.setText(state.selectAllOperation.titleRes)
            binding.selectionStateTextView.text = getString(R.string.selection_state, state.totalCheckedCount, state.totalCount)
            toolbarUpdater?.notifyChanges()
        }

        binding.selectOrClearAllTextView.setOnClickListener {
            viewModel.selectOrClear()
        }
    }

    override fun onDestroyView() {
        toolbarUpdater = null
        super.onDestroyView()
    }

    override fun onNewUpdater(updater: ToolbarUpdater) {
        toolbarUpdater = updater
    }

    override fun onCatDelete(cat: CatListItem) {
        viewModel.deleteCat(cat)
    }

    override fun onCatToggleFavorite(cat: CatListItem) {
        viewModel.toggleFavorite(cat)
    }

    override fun onCatChosen(cat: CatListItem) {
        val direction = CatsListFragmentDirections
            .actionNavCatsListFragmentToNavCatDetailsFragment(catId = cat.id)
        findNavController().navigate(direction)
    }

    override fun onCatToggle(cat: CatListItem) {
        viewModel.toggleSelection(cat)
    }
}