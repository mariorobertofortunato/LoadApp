package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**Variable and properties declaration/initiation*/
    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()
    private var progress = 0.0

    //custom attrs declaration (as "null")
    private var defaultBackgroundColor = 0
    private var progressBarColor = 0
    private var borderColor = 0
    private var defaultText: String? = null
    private var textColor = 0
    private var borderWidth = 14.0f



    //observe the state of the button
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new -> }

    //paint object initialization with default settings
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    //TODO capire bene cosa fa updatelistener
    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }

    /**View initialization = Anim inflater + button init with the default attributes from the attrs.xml*/
    init {
        //set the button clickable
        isClickable=true

        valueAnimator = AnimatorInflater.loadAnimator(context,R.animator.progress_animator) as ValueAnimator
        valueAnimator.addUpdateListener(updateListener)

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultBackgroundColor = getColor(R.styleable.LoadingButton_defaultBackgroundColor, 0)
            progressBarColor = getColor(R.styleable.LoadingButton_progressBarColor, 0)
            borderColor = getColor(R.styleable.LoadingButton_borderColor,0)
            defaultText = getString(R.styleable.LoadingButton_defaultText)
            textColor = getColor(R.styleable.LoadingButton_textColor,0)
        }
    }

    /**"Drawing" section*/
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawText(canvas)
        drawBorder(canvas)
    }
            /**Draws the rectangle background */
            private fun drawBackground(canvas: Canvas) {
                //Default background color
                paint.color = defaultBackgroundColor
                paint.style = Paint.Style.FILL
                canvas.drawRect(0f,0f,width.toFloat(),height.toFloat(), paint)
                //Background color change = "progress bar".
                // When the button is clicked its state changes and triggers the progress anim
                if (buttonState == ButtonState.Loading) {
                    paint.color = progressBarColor
                    canvas.drawRect(0f,0f,width*(progress/100).toFloat(),height.toFloat(),paint)
                }
            }
            /**Draws the text*/
            private fun drawText(canvas: Canvas) {
                paint.color = textColor
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(
                    defaultText ?: "", (canvas.width/2).toFloat(), ((canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2)), paint)
            }
            /**Draws the border of the rectangle*/
            //For some reason this method must be called AFTER the text method, otherwise it messes it al up
            private fun drawBorder(canvas: Canvas) {
                paint.color = borderColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = borderWidth
                canvas.drawRect(0f,0f,width.toFloat(),height.toFloat(), paint)
            }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    /**Methods*/

    //When button clicked change the state of the button and start the anim
    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed)
            buttonState = ButtonState.Loading
        valueAnimator.start()
        return true
    }

    private fun startAnimation() {
        valueAnimator.start()
    }



}