package com.mcmouse88.customviewlev2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

typealias OnCellActionListener = (row: Int, column: Int, field: TicTacToeField) -> Unit

class TicTacToeView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, R.style.DefaultTicTacToeFieldStyle)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.ticTacToeFieldStyle
    )

    constructor(context: Context) : this(context, null)

    /**
     * Метод [invalidate()] запускает перерисовку компонента. Метод [requestLayout()] отвечает за
     * изменения размера компонента. Далее чтобы назначить слушателей нажатий для элементов
     * view мы в свойстве (и методе [onDetachedFromWindow()]) удаляем слушаетеля у старого
     * значения (при изменении view), и у нового значение (и в методе [onAttachedToWindow()]
     * добавляем его.
     */
    var ticTacToeField: TicTacToeField? = null
        set(value) {
            field?.listeners?.remove(listener)
            field = value
            value?.listeners?.add(listener)
            updateViewSize()
            requestLayout()
            invalidate()
        }

    var actionListener: OnCellActionListener? = null

    private val listener: OnFieldChangedListener = {
        invalidate()
    }

    /**
     * Переменный для безопасной зоны для отрисовки компонента. Класс [RectF] рисует
     * прямоугольник, в параметрах которого свойства типа [Float]
     */
    private val fieldRect = RectF(0f, 0f, 0f, 0f)
    private var cellSize = 0f
    private var cellPadding = 0f

    private var currentRow = -1
    private var currentColumn = -1

    private val cellRect = RectF()

    private var playerOneColor by Delegates.notNull<Int>()
    private var playerTwoColor by Delegates.notNull<Int>()
    private var gridColor by Delegates.notNull<Int>()

    private lateinit var playerOnePaint: Paint
    private lateinit var playerTwoPaint: Paint
    private lateinit var gridPaint: Paint
    private lateinit var currentCellPaint: Paint

    /**
     * Чтобы можно было рисовать в режиме предпросмотра, нужно в блок добавить проверку на
     * [isInEditMode], и если true установить значения компонента, тогда при запуске программа
     * проигнорирует эти значения
     */
    init {
        if (attrs != null) {
            initAttributes(attrs, defStyleAttr, defStyleRes)
        } else {
            initDefaultColors()
        }
        initPaints()

        if (isInEditMode) {
            ticTacToeField = TicTacToeField(8, 8)
            ticTacToeField?.setCell(4, 2, Cell.PLAYER_1)
            ticTacToeField?.setCell(4, 3, Cell.PLAYER_2)
        }

        isFocusable = true
        isClickable = true


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultFocusHighlightEnabled = false
        }
    }

    private fun initAttributes(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.TicTacToeView,
            defStyleAttr,
            defStyleRes
        )

        playerOneColor =
            typedArray.getColor(R.styleable.TicTacToeView_playerOneColor, PLAYER_ONE_DEFAULT_COLOR)
        playerTwoColor =
            typedArray.getColor(R.styleable.TicTacToeView_playerTwoColor, PLAYER_TWO_DEFAULT_COLOR)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, GRID_DEFAULT_COLOR)
        typedArray.recycle()
    }

    private fun initDefaultColors() {
        playerOneColor = PLAYER_ONE_DEFAULT_COLOR
        playerTwoColor = PLAYER_TWO_DEFAULT_COLOR
        gridColor = GRID_DEFAULT_COLOR
    }

    /**
     * Для рисования элементов создадим кисти, используя класс [Paint]. Флаг [ANTI_ALIAS_FLAG]
     * более медленная но качественная и детальная прорисовка. Также для кисти нужно задать цвет
     * через свойство color, и установить стиль элемента который будем рисовать, в данном случае
     * мы возьмем stroke так как нам нужно рисовать линии
     */
    private fun initPaints() {
        playerOnePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        playerOnePaint.color = playerOneColor
        playerOnePaint.style = Paint.Style.STROKE
        playerOnePaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            3f,
            resources.displayMetrics
        )

        playerTwoPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        playerTwoPaint.color = playerTwoColor
        playerTwoPaint.style = Paint.Style.STROKE
        playerTwoPaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            3f,
            resources.displayMetrics
        )

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            resources.displayMetrics
        )

        currentCellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        currentCellPaint.color = Color.rgb(230, 230, 230)
        currentCellPaint.style = Paint.Style.FILL
    }

    /**
     * Вызывается когда view уже создано, присоединено к окну отображения, и может
     * взаимодействовать с другии компонентами
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ticTacToeField?.listeners?.add(listener)
    }

    /**
     * Обратная функция, вызывается когда view уничтожено и отсоединено от экрана
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ticTacToeField?.listeners?.remove(listener)
    }

    /**
     * Методы [onMeasure()] и [onSizeChanged()] отвечают за изменение размеров компонента.
     * Отличаются тем, что [onMeasure()] пытается договорится с компоновщиком об изменении
     * размера (чтобы это не значило), а [onSizeChanged()] он уже вызывается по факту, когда
     * компоновщик назначил определенные размеры компоненту, и он вызывается после метода
     * [onMeasure()], когда компановщик уже назначил размеры для компонента. В метод
     * [onMeasure()] попадает два параметра, та длина и высота, которую компановщие определил
     * для компонента, то есть рекомендуемый размер, а также эти переменные внутри себя содержат
     * ограничения размеров, которые применяются к компоненту, но об этом похже.
     * При переопределнии данного метода, родительскую реализацию удаляем. При переопределнии
     * метода, нужно проанализировать высоту и ширину, которую нам дает компоновщик, а также
     * проанализировать ограничения, которые применяются компоновщиком к размерам, ну и
     * соотвественно предложить свои размеры компановщику, но окончательное решение все равно
     * будет за компановщиком. Сначала нужно определить минимальную высоту и ширину компонента,
     * для этошл мы берем свойство [suggestedMinimumWidth] и [suggestedMinimumHeight]] (которые
     * вычисляется из значения минимальной высоты и ширины у background компонента, и аттрибутов
     * minWidth и minHeight), а также складываем их с паддингами. Далее зададим значение, которое
     * мы хотим установить для нашего компонента (константа [DESIRED_CELL_SIZE]) в dp, далее мы
     * должны перевести его в пиксели, так как внутри View мы работаем непосредственно с пикселями.
     * Для этого вызовем статический метод [applyDimension()] класса [TypedValue], в который
     * мы переадем в качестве параметров единицу, из которой мы переводим в пиксели, далее само
     * занчение и ссылку на [displayMetrics], который находится в ресурсах. Таким образом мы
     * получаем размер ячейки, который мы хотим применить к нашему компоненту. Дальше нам нужно
     * взять количество строк и количество колонок. Имея количество рядов и колонок, а также
     * размер ячейки, мы можем получить значение ширины и высоты нашего компонента. При этом
     * также нужно учесть вертикальные и горизонтальные паддинги. Также нам еще нужно сравнить
     * данное значение с минимальным, и взять наибольшее. И в сам конце, чтобы применить наши
     * размеры, нужно вызвать метод [setMeasuredDimension()], внутри которого сравнить
     * наши размеры с размерами предложенными компоновщиком (теми которые приходят в параметрах)
     * при помощи метода [resolveSize()], который сравнит значения компановщика с имеющимися
     * внутри них флагами (которые имеют три значения, строгое ограничение, то есть можно
     * использовать только значение, которое предложил компановщих, любое значение, а также
     * любое значение, но которое не может быть больше определнного, более подробно об этом можно
     * посмортеть в реализации самого метода).
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val cellSizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DESIRED_CELL_SIZE,
            resources.displayMetrics
        ).toInt()

        val rows = ticTacToeField?.rows ?: 0
        val columns = ticTacToeField?.columns ?: 0

        val desiredWidth = max(minWidth, columns * cellSizeInPixels + paddingLeft + paddingRight)
        val desiredHeight = max(minHeight, rows * cellSizeInPixels + paddingTop + paddingRight)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    /**
     * В данный метод нам приходит уже готовая(новая) высота и ширина нашего компонента, и в этом
     * методе мы должны с учетом паддингов рассчитать безопасную зону, где мы будем рисовать
     * интерфейс компонента.
     */
    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)
        updateViewSize()
    }

    private fun updateViewSize() {
        val field = ticTacToeField ?: return

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize * 0.2f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    /**
     * Чтобы начать рисовать нужно переопределить метод [onDraw()]. Метод должен быть
     * максимально оптимизирован, и в нем не рекомендуется создавать новые объекты.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (ticTacToeField == null) return
        if (cellSize == 0f) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        drawGrid(canvas)
        drawCurrentCell(canvas)
        drawCells(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val field = this.ticTacToeField ?: return
        val xStart = fieldRect.left
        val xEnd = fieldRect.right
        for (i in 0..field.rows) {
            val y = fieldRect.top + cellSize * i
            canvas.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = fieldRect.top
        val yEnd = fieldRect.bottom
        for (i in 0..field.columns) {
            val x = fieldRect.left + cellSize * i
            canvas.drawLine(x, yStart, x, yEnd, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas) {
        val field = ticTacToeField ?: return

        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                val cell = field.getCell(row, column)
                if (cell == Cell.PLAYER_1) drawPlayerOne(canvas, row, column)
                else if (cell == Cell.PLAYER_2) drawPlayerTwo(canvas, row, column)
            }
        }
    }

    private fun drawCurrentCell(canvas: Canvas) {
        val field = ticTacToeField ?: return
        if (currentRow < 0 && currentColumn < 0 && currentRow >= field.rows && currentColumn >= field.columns) return
        val cell = getCellRect(currentRow, currentColumn)
        canvas.drawRect(
            cell.left - cellPadding,
            cell.top - cellPadding,
            cell.right + cellPadding,
            cell.bottom + cellPadding,
            currentCellPaint
        )
    }

    private fun drawPlayerOne(canvas: Canvas, row: Int, column: Int) {
        val cell = getCellRect(row, column)
        canvas.drawLine(
            cell.left,
            cell.top,
            cell.right,
            cell.bottom,
            playerOnePaint
        )
        canvas.drawLine(
            cell.right,
            cell.top,
            cell.left,
            cell.bottom,
            playerOnePaint
        )
    }

    private fun drawPlayerTwo(canvas: Canvas, row: Int, column: Int) {
        val cell = getCellRect(row, column)
        canvas.drawCircle(cell.centerX(), cell.centerY(), cell.width() / 2, playerTwoPaint)
    }

    private fun getCellRect(row: Int, column: Int): RectF {
        cellRect.left = fieldRect.left + column * cellSize + cellPadding
        cellRect.top = fieldRect.top + row * cellSize + cellPadding
        cellRect.right = cellRect.left + cellSize - cellPadding * 2
        cellRect.bottom = cellRect.top + cellSize - cellPadding * 2
        return cellRect
    }

    /**
     * Так как нам нужно слушать только касания экрана, то мы будем только использовать метод
     * [onTouchEvent()], для более сложных действий (скроллинг, драг, масштабирование) нужно
     * использвать класс [GestureDetector]
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                updateCurrentCell(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                updateCurrentCell(event)
            }
            MotionEvent.ACTION_UP -> {
                return performClick()
            }
        }
        return false
    }

    /**
     * Метод [performClick()] используется для телефонов без сенсорного экрана, поэтому вместе
     * с методом [onTouchEvent()] и другими методами работы с сенсорным экраном рекомендуется
     * также переопределять и его (об этом также предупреждает студия)
     */
    override fun performClick(): Boolean {
        super.performClick()
        val field = this.ticTacToeField ?: return false
        val row = currentRow
        val column = currentColumn
        return if (row >= 0 && column >= 0 && row < field.rows && column < field.columns) {
            actionListener?.invoke(row, column, field)
            true
        } else false
    }

    private fun getRow(event: MotionEvent): Int {
        return floor((event.y - fieldRect.top) / cellSize).toInt()
    }

    private fun getColumn(event: MotionEvent): Int {
        return floor((event.x - fieldRect.left) / cellSize).toInt()
    }

    private fun updateCurrentCell(event: MotionEvent) {
        val field = ticTacToeField ?: return
        val row = getRow(event)
        val column = getColumn(event)
        if (row >= 0 && column >= 0 && row < field.rows && column < field.columns) {
            if (currentRow != row || currentColumn != column) {
                currentRow = row
                currentColumn = column
                invalidate()
            } else {
                currentRow = -1
                currentColumn = -1
                invalidate()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> moveCurrentCell(1, 0)
            KeyEvent.KEYCODE_DPAD_UP -> moveCurrentCell(-1, 0)
            KeyEvent.KEYCODE_DPAD_LEFT -> moveCurrentCell(0, -1)
            KeyEvent.KEYCODE_DPAD_RIGHT -> moveCurrentCell(0, 1)
            else -> return super.onKeyDown(keyCode, event)
        }
    }

    private fun moveCurrentCell(rowDiff: Int, columnDif: Int): Boolean {
        val field = this.ticTacToeField ?: return false
        if (currentRow == -1 || currentColumn == -1) {
            currentRow =0
            currentColumn =0
            invalidate()
            return true
        } else {
            if (currentColumn + columnDif < 0) return false
            if (currentColumn + columnDif >= field.columns) return false
            if (currentRow + rowDiff < 0) return false
            if (currentRow + rowDiff >= field.rows) return false
            currentColumn += columnDif
            currentRow += rowDiff
            invalidate()
            return true
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.currentRow = currentRow
        savedState.currentColumn = currentColumn
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        currentRow = savedState.currentRow
        currentColumn = savedState.currentColumn
    }

    class SavedState : BaseSavedState {

        var currentRow by Delegates.notNull<Int>()
        var currentColumn by Delegates.notNull<Int>()

        constructor(superState: Parcelable) : super(superState)
        constructor(parcel: Parcel) : super(parcel) {
            currentRow = parcel.readInt()
            currentColumn = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(currentRow)
            out.writeInt(currentColumn)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = Array(size) { null }
            }
        }
    }

        companion object {
            private const val PLAYER_ONE_DEFAULT_COLOR = Color.BLUE
            private const val PLAYER_TWO_DEFAULT_COLOR = Color.RED
            private const val GRID_DEFAULT_COLOR = Color.GRAY

            private const val DESIRED_CELL_SIZE = 50F
        }
    }