package com.anderson.danny.ceasercipher

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat


/**
 * TODO: document your custom view class.
 */
class ScrollSelector : HorizontalScrollView {

    private lateinit var linearLayout: LinearLayout

    private var _textDimension: Float = 0f // TODO: use a default from R.dimen...
    private var _selectableTextList: Array<CharSequence> = arrayOf()
    private var _buttonBackground: Drawable? = null

    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f
    private var selectedButton: Button? = null

    private var onButtonSelectedListener: OnButtonSelectedListener? = null




    /**
     * In the example view, this dimension is the font size.
     */
    var textDimension: Float
        get() = _textDimension
        set(value) {
            _textDimension = value
        }



    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        linearLayout = LinearLayout(context, attrs)
        val layoutParams = LinearLayout.LayoutParams(context, attrs)
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        linearLayout.layoutParams = layoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL
        addView(linearLayout)
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ScrollSelector, defStyle, 0
        )

        _selectableTextList = a.getTextArray(R.styleable.ScrollSelector_selectableTextList)

        if (a.hasValue(R.styleable.ScrollSelector_buttonBackground)) {
            _buttonBackground = a.getDrawable(R.styleable.ScrollSelector_buttonBackground)
        }
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        _textDimension = a.getDimension(
            R.styleable.ScrollSelector_textDimension,
            textDimension
        )

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        // Update TextPaint and text measurements from attributes
        addChildren(attrs)
    }

    fun selectFirstOption() {
        (getChildAt(0) as ViewGroup).getChildAt(0).performClick()
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom


    }


    private fun addChildren(attrs: AttributeSet?) {
        var button: Button
        _selectableTextList.forEach {
            button = Button(context, attrs)
            button.text = it
            button.textSize = textDimension

            val states = arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            )
            val accentColor =
                ResourcesCompat.getColor(context.resources, R.color.colorAccent, context.resources.newTheme())
            val colors = intArrayOf(accentColor, Color.WHITE)

            val myList = ColorStateList(states, colors)
            button.setTextColor(myList)
            button.setOnClickListener { button ->
                selectedButton?.isSelected = false
                selectedButton = button as Button
                button.isSelected = true
                onButtonSelectedListener?.onButtonSelected(button)
            }
            button.background = ContextCompat.getDrawable(context, R.drawable.selectable_no_bg)
            linearLayout.addView(button)
        }
    }

    fun setOnClickListener(listener: OnButtonSelectedListener) {
        onButtonSelectedListener = listener
    }

    interface OnButtonSelectedListener {
        fun onButtonSelected(button: Button)
    }
}
