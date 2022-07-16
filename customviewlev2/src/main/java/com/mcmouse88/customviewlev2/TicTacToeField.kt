package com.mcmouse88.customviewlev2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Cell {
    PLAYER_1, PLAYER_2, EMPTY
}

typealias OnFieldChangedListener = (field: TicTacToeField) -> Unit

class TicTacToeField private constructor(
    val rows: Int,
    val columns: Int,
    private val cells: Array<Array<Cell>>
) {

    constructor(rows: Int, columns: Int) : this(
        rows,
        columns,
        Array(rows) { Array(columns) { Cell.EMPTY } }
    )

    val listeners = mutableSetOf<OnFieldChangedListener>()

    fun getCell(row: Int, column: Int): Cell {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return Cell.EMPTY
        return cells[row][column]
    }

    fun setCell(row: Int, column: Int, cell: Cell) {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return
        if (cells[row][column] != cell) {
            cells[row][column] = cell
            listeners.forEach { it?.invoke(this) }
        }
    }

    fun saveState(): Memento {
        return Memento(rows, columns, cells)
    }

    @Parcelize
    data class Memento(
        private val rows: Int,
        private val columns: Int,
        private val cells: Array<Array<Cell>>
    ): Parcelable {
        fun restoreField(): TicTacToeField {
            return TicTacToeField(rows, columns, cells)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Memento

            if (rows != other.rows) return false
            if (columns != other.columns) return false
            if (!cells.contentDeepEquals(other.cells)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = rows
            result = 31 * result + columns
            result = 31 * result + cells.contentDeepHashCode()
            return result
        }
    }
}