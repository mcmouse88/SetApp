package com.mcmouse88.paging_library.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse88.paging_library.Repositories
import com.mcmouse88.paging_library.adapters.DefaultLoadStateAdapter
import com.mcmouse88.paging_library.adapters.TryAgainAction
import com.mcmouse88.paging_library.adapters.UsersAdapter
import com.mcmouse88.paging_library.databinding.ActivityMainBinding
import com.mcmouse88.paging_library.simpleScan
import com.mcmouse88.paging_library.viewModelCreator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private lateinit var mainLoadStateHolder: DefaultLoadStateAdapter.Holder

    private val mainViewModel by viewModelCreator { MainViewModel(Repositories.usersRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Repositories.init(applicationContext)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        setupUsersList()
        setupSearchInput()
        setupSwipeToRefresh()
        setupEnableErrorsCheckBox()
    }

    /**
     * Чтобы объеденить два адаптреа, в основном адаптере вызовем метод [withLoadStateFooter()],
     * в который в качестве параметра передадим второй адаптре, отвечающий за отображение
     * ProgressBar и сообщения об ошибке, после чего этот объединенный адаптре назначается
     * RecyclerView.
     */
    private fun setupUsersList() {
        val adapter = UsersAdapter()
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)
        val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)

        binding.apply {
            rvUsers.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUsers.adapter = adapterWithLoadState
            (rvUsers.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        }

        mainLoadStateHolder = DefaultLoadStateAdapter.Holder(
            binding.loadStateView,
            binding.swipeRefreshLayout,
            tryAgainAction
        )

        observeUsers(adapter)
        observeLoadState(adapter)

        handleScrollingToTopWhenSearching(adapter)
        handleListVisibility(adapter)
    }

    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener {
            mainViewModel.setSearchBy(it.toString())
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            mainViewModel.refresh()
        }
    }

    private fun observeUsers(adapter: UsersAdapter) {
        lifecycleScope.launch {
            mainViewModel.usersFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeLoadState(adapter: UsersAdapter) {
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200)
                .collectLatest { state ->
                    mainLoadStateHolder.bind(state.refresh)
                }
        }
    }

    private fun handleScrollingToTopWhenSearching(adapter: UsersAdapter) = lifecycleScope.launch {
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.rvUsers.scrollToPosition(0)
                }
            }
    }

    private fun handleListVisibility(adapter: UsersAdapter) = lifecycleScope.launch {
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.rvUsers.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun getRefreshLoadStateFlow(adapter: UsersAdapter): Flow<LoadState> {
        return adapter.loadStateFlow.map { it.refresh }
    }

    private fun setupEnableErrorsCheckBox() {
        lifecycleScope.launch {
            mainViewModel.isErrorsEnabled.collectLatest {
                binding.cbError.isChecked = it
            }
        }
        binding.cbError.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.setEnableErrors(isChecked)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}