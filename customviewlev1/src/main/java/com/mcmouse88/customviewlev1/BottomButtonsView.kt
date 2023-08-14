package com.mcmouse88.customviewlev1

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.mcmouse88.customviewlev1.databinding.PartButtonBinding

/**
 * Чтобы сделать обработку нажатий на кнопки в нашей View, создадим enum class, в котором
 * назначим значение для каждой кнопки.
 */
enum class BottomButtonAction {
    POSITIVE, NEGATIVE
}

/**
 * Для обработки нажатий создадим typealias, который в качестве параметра будет принимать
 * значение из enum class
 */
typealias OnBottomButtonActionListener = (BottomButtonAction) -> Unit

/**
 * Чтобы создать свой компонент на уровне класса, нужно использовать унаследоваться от
 * родительского элемента внутри XML файла. В случае с [ConstraintLayout] если используется
 * версия API выше 21, то лучше использовать конструктор с четыремя элементами. Если ниже, то
 * нужно использовать конструктор с тремя параметрами. Параметр [AttributeSet] отвечает за
 * стиль компонента, если не передавать, то будет использоваться стиль по умолчанию. Также
 * желательно переопределить все конструкторы у имеющегося родительского класса. Если у нас есть
 * готовые аттрибуты и стили, то передаем их в переопределнные конструкторы, если нет, то
 * передаем 0. Первый конструктор с двумя параметрами используется для создания компонента
 * из кода (то есть практически никогда), и ему нужен только контекст в качестве параметра.
 */
class BottomButtonsView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * Теперь немного о стилях, изначально у нас береться значение из аттрибута в самом XML
     * файле, в данном случае в [activity_main] во view
     * [com.mcmouse88.customviewlev1.BottomButtonsView]. Если его удалить, то он будет браться
     * из стиля указанного непосредственно в самом view, где и был указан предыдущий
     * аттрибут, то есть
     * ```css
     * style="@style/ConcreteMyButtonStyle"
     * ```
     * Далее если все это не указано, то аттрибут берется из стиля, указанного непосредственно в
     * самой теме, то есть в проекте в [DefaultBottomButtonStyle]. Если же и в теме ничего не
     * указано, то тогда значение аттрибута будет браться из стиля по умолчанию, а именно в XML
     * файле values
     * ```css
     * <attr name="bottomButtonStyle" format="reference" />
     * ```
     * который мы в классе компонента прописали третьим аргументом в конструкторе. Если же это
     * ничего не указано, то тогда значение аттрибута будет браться из стиля по умолчанию,
     * [MyBottomButtonStyle], который определен у нас в XML файле themes, и указан в качестве
     * четвертого параметра в конструкторе. Если же все стили выше не объявлены, тогда
     * используются значения по умолчанию, объявленные при парсинге аттрибутов (у нас в
     * функции [initializeAttribute()]
     */
    private val binding: PartButtonBinding

    private var listener: OnBottomButtonActionListener? = null

    var isProgressMode: Boolean = false
        set(value) {
            field = value
            with(binding) {
                if (value) {
                    btNegative.visibility = GONE
                    btPositive.visibility = GONE
                    progress.visibility = VISIBLE
                } else {
                    btNegative.visibility = VISIBLE
                    btPositive.visibility = VISIBLE
                    progress.visibility = GONE
                }
            }
        }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, R.style.MyBottomButtonStyle)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.bottomButtonStyle
    )

    constructor(context: Context) : this(context, null)

    /**
     * Итак чтобы инициализировать наш компонент при создании, используется блок [init], в котором
     * мы создаем [LayoutInflater] и надуваем нашу разметку, через метод [inflate()], куда
     * передаем в качестве параметров наш XML файл с разметкой, [ViewGroup] (так как
     * мы используем [ConstraintLayout] в качестве [ViewGroup], то просто передадим this, и
     * указываем присоединять ли компоненты, которые находятся внутри XML файла к нашей view, то
     * есть указываем, что нужно передав true. Также подключим к view [ViewBinding], чтобы потом
     * с элементами view можно было работать через него.
     */

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.part_button, this, true)
        binding = PartButtonBinding.bind(this)
        initializeAttribute(attrs, defStyleAttr, defStyleRes)
        initializeListener()
    }

    /**
     * Также привяжем наши аттрибуты, которые мы создали в XML файле attrs в папке values.
     * Сначала проверим передавались ли какие-либо аттрибуты, если нет то сразу выходим, так как
     * их нет и работать с ними не нужно. Далее нам нужен объект типа [TypedArray], который
     * позволит парсить аттрибуты. Чтобы получить объект класса [TypedArray] у контекста нужно
     * вызвать метод [obtainStyledAttributes()], и передать в качестве параметров все аттрибуты,
     * которые приходят в конструктор нашей view, а также XML файл с созданными нами аттрибутами.
     * Также по кончанию работы с [TypedArray] нужно освободить ресурсы, вызвав метод [recycle()].
     * После парсинга аттрибутов, они станут доступны для просмотра в XML макете (только нужно
     * предварительно собрать проект, а также после каждого изменения в парсинге, чтобы они
     * были применены, тоже нужно собрать проект).
     */
    private fun initializeAttribute(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.BottomButtonsView,
            defStyleAttr,
            defStyleRes
        )

        with(binding) {
            val positiveButtonText =
                typedArray.getString(R.styleable.BottomButtonsView_bottomPositiveButtonText)
            setButtonPositiveText(positiveButtonText)

            val negativeButtonText =
                typedArray.getString(R.styleable.BottomButtonsView_bottomNegativeButtonText)
            setOnNegativeButtonText(negativeButtonText)

            val positiveBackgroundColor = typedArray.getColor(
                R.styleable.BottomButtonsView_bottomPositiveBackgroundColor,
                resources.getColor(R.color.purple_700, resources.newTheme())
            )
            btPositive.backgroundTintList = ColorStateList.valueOf(positiveBackgroundColor)

            val negativeBackgroundColor = typedArray.getColor(
                R.styleable.BottomButtonsView_bottomNegativeBackgroundColor,
                Color.WHITE
            )
            btNegative.backgroundTintList = ColorStateList.valueOf(negativeBackgroundColor)

            isProgressMode =
                typedArray.getBoolean(R.styleable.BottomButtonsView_bottomProgressMode, false)

        }

        typedArray.recycle()
    }

    private fun initializeListener() {
        binding.btPositive.setOnClickListener {
            this.listener?.invoke(BottomButtonAction.POSITIVE)
        }

        binding.btNegative.setOnClickListener {
            this.listener?.invoke(BottomButtonAction.NEGATIVE)
        }
    }

    fun setOnBottomButtonClickListener(listener: OnBottomButtonActionListener?) {
        this.listener = listener
    }

    fun setButtonPositiveText(text: String?) {
        binding.btPositive.text = text ?: context.getString(R.string.confirm)
    }

    fun setOnNegativeButtonText(text: String?) {
        binding.btNegative.text = text ?: context.getString(R.string.cancel)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState() ?: BaseSavedState.EMPTY_STATE
        val savedState = SavedState(superState)
        savedState.positiveButtonText = binding.btPositive.text.toString()
        savedState.negativeButtonText = binding.btNegative.text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        binding.btPositive.text = savedState.positiveButtonText
        binding.btNegative.text = savedState.negativeButtonText
    }

    class SavedState : BaseSavedState {

        var positiveButtonText: String? = null
        var negativeButtonText: String? = null

        constructor(superStare: Parcelable) : super(superStare)
        constructor(parcel: Parcel) : super(parcel) {
            positiveButtonText = parcel.readString()
            negativeButtonText = parcel.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(positiveButtonText)
            out.writeString(negativeButtonText)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return Array(size) { null }
                }
            }
        }
    }
}