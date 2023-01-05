package com.mcmouse88.list_adapter.sample_1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mcmouse88.list_adapter.databinding.ActivityCatsBinding
import com.mcmouse88.list_adapter.model.Cat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatsActivity : AppCompatActivity() {

    private var _binding: ActivityCatsBinding? = null
    private val binding: ActivityCatsBinding
        get() = _binding ?: throw NullPointerException("ActivityCatsBinding is null")

    private val viewModel by viewModels<CatsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCatsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        setupAdapter()
    }

    private fun setupAdapter() {
        val adapter = CatsAdapter(object : CatsAdapter.Listener {
            
            override fun onChooseCat(cat: Cat) {
                Toast.makeText(
                    this@CatsActivity,
                    "${cat.name} meow-meows",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onToggleFavorite(cat: Cat) {
                viewModel.toggleFavorite(cat)
            }

            override fun onDeleteCat(cat: Cat) {
                viewModel.deleteCat(cat )
            }
        })

        (binding.rvCats.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.rvCats.adapter = adapter
        
        viewModel.catsLiveData.observe(this) {
            adapter.submitList(it)
        }
    }
}