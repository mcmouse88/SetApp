package com.mcmouse88.fragment_from_listview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.add
import com.mcmouse88.fragment_from_listview.databinding.FragmentCatsDetailBinding
import com.mcmouse88.fragment_from_listview.model.Cat
import com.mcmouse88.fragment_from_listview.model.CatService

class MainActivity : AppCompatActivity(), AppContract {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, CatsListFragment())
                .commit()
        }


    }

    override val carsService: CatService
        get() = (applicationContext as App).catService

    override fun launchCatDetailScreen(cat: Cat) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CatsDetailFragment.newInstance(cat))
            .addToBackStack(null)
            .commit()
    }
}