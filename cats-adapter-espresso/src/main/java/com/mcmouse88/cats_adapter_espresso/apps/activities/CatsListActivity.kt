package com.mcmouse88.cats_adapter_espresso.apps.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mcmouse88.catadapterespresso.databinding.FragmentCatsBinding
import com.mcmouse88.cats_adapter_espresso.CatsAdapterListener
import com.mcmouse88.cats_adapter_espresso.catsAdapter
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatListItem
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatsListActivity : AppCompatActivity(), CatsAdapterListener {

    private val viewModel by viewModels<CatsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentCatsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val adapter = catsAdapter(this)
        (binding.rvCats.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.rvCats.adapter = adapter
        viewModel.catsLiveData.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun onCatDelete(cat: CatListItem.CatItem) {
        viewModel.deleteCat(cat)
    }

    override fun onCatToggleFavorite(cat: CatListItem.CatItem) {
        viewModel.toggleCat(cat)
    }

    override fun onCatChosen(cat: CatListItem.CatItem) {
        val intent = Intent(this, CatDetailsActivity::class.java)
        intent.putExtra(CatDetailsActivity.EXTRA_CAT_ID, cat.id)
        startActivity(intent)
    }
}