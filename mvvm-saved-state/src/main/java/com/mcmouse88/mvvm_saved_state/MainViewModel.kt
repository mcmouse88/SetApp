package com.mcmouse88.mvvm_saved_state

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class MainViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _seed = savedStateHandle.getLiveData<Long>(KEY_SEED)
    val squares: LiveData<Square> = Transformations.map(_seed) { createSquares(it) }

    init {
        if (!savedStateHandle.contains(KEY_SEED)) {
            savedStateHandle[KEY_SEED] = Random.nextLong()
        }
    }

    fun generateSquares() {
        _seed.value = Random.nextLong()
    }

    private fun createSquares(seed: Long): Square {
        val random = Random(seed)
        return Square(
            size = random.nextInt(5, 11),
            colorProducer = { -random.nextInt(0xFFFFFF) }
        )
    }

    companion object {
        const val KEY_SEED = "key_seed"
    }
}