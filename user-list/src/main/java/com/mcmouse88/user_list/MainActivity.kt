package com.mcmouse88.user_list

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcmouse88.user_list.databinding.ActivityMainBinding
import com.mcmouse88.user_list.model.*

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private lateinit var adapter: UsersAdapter

    private val userService: UserService
        get() = (applicationContext as App).userService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        adapter = UsersAdapter(object : UserActionListener {

            override fun onUserMove(user: User, moveBy: Int) {
                userService.moveUser(user, moveBy)
            }

            override fun onUserDelete(user: User) {
                userService.deleteUser(user)
            }

            override fun onUserDetail(user: User) {
                Toast.makeText(this@MainActivity, "User: ${user.name}", Toast.LENGTH_SHORT).show()
            }
        })

        val layoutManager = LinearLayoutManager(this)
        binding.rvListUsers.layoutManager = layoutManager
        binding.rvListUsers.adapter = adapter

        userService.addListener(userListener)
    }

    private val userListener: UserListener = {
        adapter.users = it
    }

    override fun onDestroy() {
        _binding = null
        userService.removeListener(userListener)
        super.onDestroy()
    }
}