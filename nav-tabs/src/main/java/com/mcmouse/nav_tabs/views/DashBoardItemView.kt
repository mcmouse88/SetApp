package com.mcmouse.nav_tabs.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.databinding.PartDashboardItemBinding
import com.mcmouse.nav_tabs.models.boxes.entities.Box

class DashBoardItemView(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attributeSet, defStyleAttr, R.style.DefaultDashBoardItemStyle)

    constructor(
        context: Context,
        attributeSet: AttributeSet?
    ) : this(context, attributeSet, R.attr.dashBoardItemStyle)

    constructor(context: Context) : this(context, null)

    private val binding: PartDashboardItemBinding

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.part_dashboard_item, this, true)
        binding = PartDashboardItemBinding.bind(this)
        parseAttributes(attributeSet, defStyleAttr, defStyleRes)
    }

    fun setBox(box: Box) {
        val colorName = context.getString(box.colorNameRes)
        val boxTitle = context.getString(R.string.box_title, colorName)
        setupTitle(boxTitle)
        setupColors(box.colorValue)
    }

    private fun parseAttributes(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val defaultColor = ContextCompat.getColor(context, R.color.default_dashboard_color)
        val defaultTitle = "No Title"

        val color: Int
        val title: String
        if (attributeSet != null) {
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DashBoardItemView, defStyleAttr, defStyleRes)
            color = typedArray.getColor(R.styleable.DashBoardItemView_color, defaultColor)
            title = typedArray.getString(R.styleable.DashBoardItemView_title) ?: defaultTitle
            typedArray.recycle()
        } else {
            color = defaultColor
            title = defaultTitle
        }
        setupColors(color)
        setupTitle(title)
    }

    private fun setupTitle(title: String) {
        binding.tvTitle.text = title
    }

    private fun setupColors(strokeColor: Int) {
        val bgColor = getBackgroundColor(strokeColor)
        val backgroundDrawable = GradientDrawable().apply {
            color = ColorStateList.valueOf(bgColor)
            setStroke(resources.getDimensionPixelSize(R.dimen.dashboard_item_stroke_width), strokeColor)
            cornerRadius = resources.getDimensionPixelSize(R.dimen.dashboard_item_corner_radius).toFloat()
        }
        binding.tvTitle.setTextColor(strokeColor)
        background = RippleDrawable(ColorStateList.valueOf(Color.BLACK), backgroundDrawable, null)
    }

    companion object {
        fun getBackgroundColor(color: Int): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            return Color.argb(64, red, green, blue)
        }
    }
}