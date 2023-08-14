package com.mcmouse88.file_to_server.presentation.files

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.SimpleBindingAdapter
import com.elveum.elementadapter.adapter
import com.elveum.elementadapter.addBinding
import com.elveum.elementadapter.context
import com.mcmouse88.file_to_server.R
import com.mcmouse88.file_to_server.databinding.EmptySpaceBinding
import com.mcmouse88.file_to_server.databinding.FragmentFilesBinding
import com.mcmouse88.file_to_server.databinding.ItemFileBinding
import com.mcmouse88.file_to_server.presentation.base.CustomToolbarAction
import com.mcmouse88.file_to_server.presentation.base.ToolbarAction
import com.mcmouse88.file_to_server.presentation.base.ToolbarUpdater
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilesFragment : Fragment(R.layout.fragment_files), CustomToolbarAction {

    private var _adapter: SimpleBindingAdapter<FileListItem>? = null
    private val adapter: SimpleBindingAdapter<FileListItem>
        get() = requireNotNull(_adapter) { "SimpleBindingAdapter is null" }

    private val viewModel by viewModels<FilesViewModel>()
    private var updater: ToolbarUpdater? = null

    override val action: ToolbarAction = ToolbarAction(
        iconRes = R.drawable.ic_logout,
        action = {
            viewModel.signOut()
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentFilesBinding.bind(view).apply {
            btnTryAgain.setOnClickListener { viewModel.load() }
            swipeRefresh.setOnRefreshListener { viewModel.reload() }
            btnUpload.setOnClickListener { viewModel.chooseFileAndUpload() }
            rvFiles.layoutManager = LinearLayoutManager(requireContext())
            (rvFiles.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            rvFiles.adapter = adapter
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            binding.render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        updater = null
    }

    override fun onNewUpdater(updater: ToolbarUpdater) {
        this.updater = updater
    }

    private fun FragmentFilesBinding.render(state: FilesViewModel.State) {
        progressBar.isVisible = state is FilesViewModel.State.Loading
        errorContainer.isVisible = state is FilesViewModel.State.Error
        swipeRefresh.isVisible = state is FilesViewModel.State.Files
        swipeRefresh.isRefreshing = state is FilesViewModel.State.Files && state.reloadInProgress
        uploadContainer.isVisible =
            (state as? FilesViewModel.State.Files)?.uploadInProgress ?: false
        btnUpload.isVisible = uploadContainer.isVisible.not()
        tvErrorMessage.text = (state as? FilesViewModel.State.Error)?.message
        if (state is FilesViewModel.State.Files) {
            adapter.submitList(state.files + FileListItem.Space)
        }
    }

    private fun createAdapter(): SimpleBindingAdapter<FileListItem> = adapter {
        addBinding<FileListItem.File, ItemFileBinding> {
            areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
            areContentsSame = { oldItem, newItem -> oldItem == newItem }
            bind { file ->
                tvFileName.text = file.fileName
                tvFileSize.text = file.prettySize(context())
                ivDelete.isInvisible = file.deleteInProgress
                pbDelete.isVisible = file.deleteInProgress
            }

            listeners {
                ivDelete.onClick { file ->
                    viewModel.delete(file)
                }
            }
        }

        addBinding<FileListItem.Space, EmptySpaceBinding> { /* no-op */ }
    }
}