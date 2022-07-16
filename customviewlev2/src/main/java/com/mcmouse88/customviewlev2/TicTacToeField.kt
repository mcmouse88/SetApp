package com.mcmouse88.customviewlev2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Cell {
    PLAYER_1, PLAYER_2, EMPTY
}

typealias OnFieldChangedListener = (field: TicTacToeField) -> Unit

class TicTacToeField(
    val rows: Int,
    val columns: Int,
    private val cells: Array<Array<Cell>>
) {

    constructor(rows: Int, columns: Int) : this(
        rows, columns, Array(rows) { Array(columns) { Cell.EMPTY } }
    )

    val listener = mutableSetOf<OnFieldChangedListener>()

    fun getCell(row: Int, columns: Int): Cell {
        if (row < 0 || columns < 0 || row >= rows || columns >= columns) return Cell.EMPTY
        return cells[row][columns]
    }

    fun setCell(row: Int, columns: Int, cell: Cell) {
        if (row < 0 || columns < 0 || row >= rows || columns >= columns) return
        if (cells[row][columns] != cell) {
            cells[row][columns] = cell
            listener.forEach { it?.invoke(this) }
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