package com.mcmouse88.user_list

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.user_list.databinding.ActivityMainBinding
import com.mcmouse88.user_list.model.User
import com.mcmouse88.user_list.screens.UserDetailsFragment
import com.mcmouse88.user_list.screens.UsersListFragment

class MainActivity : AppCompatActivity(), Navigator {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, UsersListFragment())
                .commit()
        }
    }

    override fun showDetail(user: User) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UserDetailsFragment.newInstance(user.id))
            .addToBackStack(null)
            .commit()
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun toast(messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}