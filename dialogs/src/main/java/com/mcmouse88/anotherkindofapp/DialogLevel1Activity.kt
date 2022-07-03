package com.mcmouse88.anotherkindofapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.anotherkindofapp.databinding.ActivityDialogLevel1Binding
import kotlin.properties.Delegates.notNull

class DialogLevel1Activity : AppCompatActivity() {

    private var _binding: ActivityDialogLevel1Binding? = null
    private val binding: ActivityDialogLevel1Binding
        get() = _binding ?: throw NullPointerException("ActivityDialogLevel1Binding is null")

    private val volume by notNull<Int>()
    private val color by notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDialogLevel1Binding.inflate(layoutInflater)
            .also { setContentView(it.root) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}