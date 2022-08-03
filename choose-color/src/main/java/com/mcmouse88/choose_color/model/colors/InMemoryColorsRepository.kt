package com.mcmouse88.choose_color.model.colors

import android.graphics.Color
import com.mcmouse88.foundation.model.coroutines.IoDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class InMemoryColorsRepository(
    private val dispatcher: IoDispatcher
) : ColorsRepository {

    private var currentColor: NamedColor = AVAILABLE_COLORS[0]

    private val listeners = mutableSetOf<ColorListener>()

    /**
     * При вызове лямбды метода [createTask] последняя строчка это возвращаемое значение, поэтому
     * можно обойтись и без [return@createTask], но оставлю для наглядности
     */
    override suspend fun getAvailableColors(): List<NamedColor> = withContext(dispatcher.value) {
        delay(2_000)
        return@withContext AVAILABLE_COLORS
    }

    override suspend fun getById(id: Long): NamedColor = withContext(dispatcher.value) {
        delay(2_000)
        return@withContext AVAILABLE_COLORS.first { it.id == id }
    }

    override suspend fun getCurrentColor(): NamedColor = withContext(dispatcher.value) {
        delay(2_000)
        return@withContext currentColor
    }

    override suspend fun setCurrentColor(color: NamedColor) = withContext(dispatcher.value) {
        delay(2_000)
        if (currentColor != color) {
            currentColor = color
            listeners.forEach { it(color) }
        }
    }

    override fun addListener(listener: ColorListener) {
        listeners += listener
    }

    override fun removeListener(listener: ColorListener) {
        listeners -= listener
    }

    companion object {
        private val AVAILABLE_COLORS = listOf(
            NamedColor(1, "Red", Color.RED),
            NamedColor(2, "Green", Color.GREEN),
            NamedColor(3, "Blue", Color.BLUE),
            NamedColor(4, "Yellow", Color.YELLOW),
            NamedColor(5, "Magenta", Color.MAGENTA),
            NamedColor(6, "Cyan", Color.CYAN),
            NamedColor(7, "Gray", Color.GRAY),
            NamedColor(8, "Navy", Color.rgb(0, 0, 128)),
            NamedColor(9, "Pink", Color.rgb(255, 20, 147)),
            NamedColor(10, "Sienna", Color.rgb(160, 82, 45)),
            NamedColor(11, "Khaki", Color.rgb(240, 230, 140)),
            NamedColor(12, "Forest Green", Color.rgb(34, 139, 34)),
            NamedColor(13, "Sky", Color.rgb(135, 206, 250)),
            NamedColor(14, "Olive", Color.rgb(107, 142, 35)),
            NamedColor(15, "Violet", Color.rgb(148, 0, 211)),
        )
    }
}