package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

//paint object initialization with default settings
private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    textSize = 60.0f
    typeface = Typeface.create("", Typeface.BOLD)
}

//custom attrs declaration (as "null")
private var defaultBackgroundColor = 0
private var borderColor = 0

private var defaultText: String? = null
private var textColor = 0


private var borderWidth = 14.0f


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private val valueAnimator = ValueAnimator()
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    /**View initialization with the default attributes (from the attrs.xml)*/
    init {
        isClickable=true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultBackgroundColor = getColor(R.styleable.LoadingButton_defaultBackgroundColor, 0)
            borderColor = getColor(R.styleable.LoadingButton_borderColor,0)
            defaultText = getString(R.styleable.LoadingButton_defaultText)
            textColor = getColor(R.styleable.LoadingButton_textColor,0)
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = Rect(0,0,width,height)
        drawBackground(canvas, rect)
        drawText(canvas, rect)
        drawBorder(canvas, rect)
    }

    /**Draws the rectangle background */
    private fun drawBackground (canvas: Canvas, rect: Rect) {
        paint.color= defaultBackgroundColor
        paint.style = Paint.Style.FILL
        canvas.drawRect(rect, paint)
    }

    /**Draws the text*/
    private fun drawText(canvas: Canvas, rect: Rect) {
        paint.color = textColor
        paint.textAlign =Paint.Align.CENTER
        canvas.drawText(defaultText ?:"" , rect.exactCenterX(), ((canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2)), paint)
    }

    /**Draws the border of the rectangle*/
    //For some reason this method must be called AFTER the text method, otherwise it messes it al up
    private fun drawBorder (canvas: Canvas, rect: Rect) {
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        canvas.drawRect(rect, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}