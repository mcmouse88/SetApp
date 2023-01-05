package com.mcmouse88.remote_mediator.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse88.remote_mediator.databinding.ActivityMainBinding
import com.mcmouse88.remote_mediator.ui.base.DefaultLoadStateAdapter
import com.mcmouse88.remote_mediator.ui.base.TryAgainAction
import com.mcmouse88.remote_mediator.ui.base.observeEvent
import com.mcmouse88.remote_mediator.ui.base.simpleScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
    get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private val viewModel by viewModels<MainViewModel>()

    private val adapter = LaunchesAdapter(object : LaunchesAdapter.Listener {
        override fun onToggleSuccessFlag(launch: LaunchUiEntity) {
            viewModel.toggleSuccessFlag(launch)
        }

        override fun onToggleCheckState(launch: LaunchUiEntity) {
            viewModel.toggleCheckState(launch)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        setupList()
        setupRefreshLayout()
        setupYearSpinner()

        observeLaunches()
        observeState()
        observeToast()

        handleListVisibility()
        handleScrollingToTop()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun setupYearSpinner() {
        val items = (listOf(null) + (2006..2025))
            .map { Year(this, it) }
            .toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

        binding.yearSpinner.adapter = adapter
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val year = parent.adapter.getItem(position) as Year
                viewModel.year = year.value
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }

        val currentYearIndex = items.indexOfFirst { it.value == viewModel.year }
        binding.yearSpinner.setSelection(currentYearIndex)
    }

    private fun setupList() {
        binding.rvLaunches.layoutManager = LinearLayoutManager(this)
        binding.rvLaunches.adapter = adapter
        (binding.rvLaunches.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.rvLaunches.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        lifecycleScope.launch {
            waitForLoad()
            val tryAgainAction: TryAgainAction = { adapter.retry() }
            val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)
            val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)
            binding.rvLaunches.adapter = adapterWithLoadState
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun observeLaunches() {
        lifecycleScope.launch {
            viewModel.launchesListFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeState() {
        val loadStateHolder = DefaultLoadStateAdapter.Holder(
            binding.loadingState,
            binding.swipeRefresh
        ) {
            adapter.refresh()
        }
        adapter.loadStateFlow
            .debounce(300)
            .onEach {
                loadStateHolder.bind(it.refresh)
            }
            .launchIn(lifecycleScope)
    }

    private fun handleListVisibility() = lifecycleScope.launch {
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.rvLaunches.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error
                        && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun handleScrollingToTop() = lifecycleScope.launch {
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collect { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    delay(200)
                    binding.rvLaunches.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: LaunchesAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

    private suspend fun waitForLoad() {
        adapter.onPagesUpdatedFlow
            .map { adapter.itemCount }
            .first { it > 0 }
    }

    private fun observeToast() {
        viewModel.toastEvent.observeEvent(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}