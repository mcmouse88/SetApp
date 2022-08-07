package com.mcmouse88.choose_color.model.colors

import android.graphics.Color
import android.util.Log
import com.mcmouse88.foundation.model.coroutines.IoDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class InMemoryColorsRepository(
    private val dispatcher: IoDispatcher
) : ColorsRepository {

    private var currentColor: NamedColor = AVAILABLE_COLORS[0]

    private val currentColorFlow = MutableSharedFlow<NamedColor>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * При вызове лямбды метода [createTask] последняя строчка это возвращаемое значение, поэтому
     * можно обойтись и без [return@createTask], но оставлю для наглядности
     */
    override suspend fun getAvailableColors(): List<NamedColor> = withContext(dispatcher.value) {
        delay(2_000)
        return@withContext AVAILABLE_COLORS
    }

    override suspend fun getById(id: Long): NamedColor = withContext(dispatcher.value) {
        delay(200)
        return@withContext AVAILABLE_COLORS.first { it.id == id }
    }

    override suspend fun getCurrentColor(): NamedColor = withContext(dispatcher.value) {
        delay(2_000)
        return@withContext currentColor
    }

    /**
     * Для того, чтобы использовать Flow при выполнении какой либо операции, нужно, чтобы эта
     * функция возвращала определенного типа (так как нужно получать цвет, то мы будем возвращать
     * Flow с типом Int), и чтобы создать Flow, мы будем использовать FlowBuilder, путем вызова
     * функции [flow()]. Данная функция в качестве аргумента принимает suspend лямбду, внутри
     * которой уже собственно и будем описывать логигу получения цвета, а также будем получать
     * прогресс выполнения операции. Этот Flow будет холодным, и конечным, в данном случае он
     * закончится, когда выполнится код внутри suspend лямбды. Чтобы опубликовать текущий прогресс
     * операции мы вызываем метод [emit()] и внутрь этого метода передаем текущий прогресс.
     * Внутри метод [flow()] нельзя вызвать метод [withContext()] (вернее вызвать можно, но при
     * запуске программы возникнет RunTimeException). Вместо вызова [withContext()], для того
     * чтобы переключить выполнение задачи в другой поток на методе [flow()] вызывается метод
     * [flowOn()], в который в качестве аргумента принимает [CoroutineEContext]. Без вызова метода
     * [flowOn()] Flow будет выполняться в том контексте, где был вызван терминальный оператор.
     * Метод [setCurrentColor()] начнет свою работу только после вызова на нем терминального
     * оператора.
     */
    override fun setCurrentColor(color: NamedColor): Flow<Int> = flow {
        Log.e("AAAAAAAA", "setCurrentColor", )
        if (currentColor != color) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(30)
                emit(progress)
            }
            currentColor = color
            currentColorFlow.emit(color)
        } else {
            emit(100)
        }
    }.flowOn(dispatcher.value)

    override fun listenCurrentColor(): Flow<NamedColor> = currentColorFlow

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