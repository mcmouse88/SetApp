package com.mcmouse88.customviewlev2

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.customviewlev2.databinding.ActivityMainBinding
import kotlin.random.Random
import com.mcmouse88.customviewlev2.TicTacToeField.Memento

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private var isFirstPlayer = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val field = savedInstanceState?.getParcelable<Memento>(KEY_FIELD)?.restoreField() ?:
        TicTacToeField(10, 10)

        binding.ticTacToeField.ticTacToeField = field
        isFirstPlayer = savedInstanceState?.getBoolean(KEY_IS_FIRST_PLAYER, true) ?: true

        binding.ticTacToeField.actionListener = { row, column, currentField ->
            val cell = currentField.getCell(row, column)
            if (cell == Cell.EMPTY) {
                if ((isFirstPlayer)) {
                    currentField.setCell(row, column, Cell.PLAYER_1)
                } else {
                    currentField.setCell(row, column, Cell.PLAYER_2)
                }
                isFirstPlayer = !isFirstPlayer
            }
        }

        binding.randomFieldButton.setOnClickListener {
            binding.ticTacToeField.ticTacToeField = TicTacToeField(
                Random.nextInt(3, 10),
                Random.nextInt(3, 10)
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val field = binding.ticTacToeField.ticTacToeField
        outState.putParcelable(KEY_FIELD, field!!.saveState())
        outState.putBoolean(KEY_IS_FIRST_PLAYER, isFirstPlayer)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val KEY_FIELD = "key_field"
        private const val KEY_IS_FIRST_PLAYER = "key_is_first_player"
    }
}