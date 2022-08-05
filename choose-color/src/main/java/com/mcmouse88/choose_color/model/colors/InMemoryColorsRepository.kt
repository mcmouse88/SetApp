package com.mcmouse88.choose_color.model.colors

import android.graphics.Color
import com.mcmouse88.foundation.model.coroutines.IoDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
        if (currentColor != color) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(30)
                emit(progress)
            }
            currentColor = color
            listeners.forEach { it(color) }
        } else {
            emit(100)
        }
    }.flowOn(dispatcher.value)

    /**
     * Далее вынесем реализацию по добавлению и удалению слушателей по смене цветов во Flow. Для
     * этого будем использовать следующий FlowBuilder, а именно метод [callbackFlow()]. Когда
     * нам нужно было превартить callback в корутину, то мы использовали метод
     * [suspendCancellableCoroutine]. [callbackFlow()] делает тоже самое, но уже не для suspend
     * функции, а для корутин. В случае с [suspendCancellableCoroutine] мы ожидали, что callback
     * сработает только один раз, но с [callbackFlow()] мы может превратить callback во Flow,
     * который может срабатывать много раз. Именно такими callback у нас являлись слушатели, которые
     * добавлялись через метод [addListener()] (сейчас этот метод уже удален из нашей программы за
     * ненадобностью), то есть те слушатели, которые оповещали о смене текущего цвета, то есть
     * каждый раз когда вызывался метод [setCurrentColor()] с добавлением нового цвета у нас
     * срабатывал слушаетель, добавленный с помощью метода [addListener()]. Теперь, чтобы
     * удалить слушателя будем использовать метод [awaitClose()], который сработает внутри Flow
     * когда внешняя корутина завершится (например если это viewModelScope, то когда у ViewModel
     * вызовестя метод onCleared, если это viewLifeCycleScope, то когда у экрана вызовется метод
     * onDestroy. Для того, чтобы передать текущий цвет, который получил слушатель, используем
     * метод [trySend()], таким образом Flow будет каждый раз оповещен о смене текущего цвета.
     * Метод [trySend()] может также вернуть результат, с помощью которого можно определен, был ли
     * это успех, ошибка, или выполнение операции было отменено. При обработке обновлений может
     * возникнуть ситуация, при которой тот кто принимает обновления, обрабатывает их дольше
     * чем тот кто их отдает (например если у нас цвет меняется каждую секунду, а обработка смены
     * цвета составляет две секунды), и в результате чего все элементы, которые еще не были
     * обработаны будут складываться в очередь (в буфер), и у этого буфера по умолчанию есть лимит.
     * Чтобы переопределить buffer, нужно на Flow вызвать метод [buffer()], в котором можно
     * определить Capacity(сколько элементов может быть максимум в буфере), а также определить, что
     * будет происходить при bufferOverFlow. [Channel.CONFLATED] сообщает Flow, что нас интересует
     * самый последний результат (текущий).
     */
    override fun listenCurrentColor(): Flow<NamedColor> = callbackFlow {
        val listener: ColorListener = {
            trySend(it)
        }
        listeners.add(listener)

        awaitClose {
            listeners.remove(listener)
        }
    }.buffer(Channel.CONFLATED)

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