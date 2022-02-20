package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**Variable and properties declaration/initiation*/
    private var widthSize = 0
    private var heightSize = 0

    //Background + border
    private var barAnimator = ValueAnimator()
    private var defaultBackgroundColor = 0
    private var progressBarColor = 0
    private var barProgress = 0.0
    private var borderColor = 0
    private var borderWidth = 14.0f
    //Text
    private var defaultText: String? = null
    private var loadingText: String? = null
    private var completedText: String? = null
    private var textColor = 0
    //Circle
    private var circleAnimator = ValueAnimator()
    private var defaultCircleColor = 0
    private var circleProgress = 0.0 //this is the starting sweep angle of the circle that gets animated

    private var buttonState: ButtonState = ButtonState.Clickable
    //private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Clickable) { p, old, new -> }

    //paint object initialization with default settings
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    //TODO capire bene cosa fa un updatelistener
    private val barUpdateListener = ValueAnimator.AnimatorUpdateListener {
        barProgress = (it.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }
    private val circleUpdateListener = ValueAnimator.AnimatorUpdateListener {
        circleProgress = (it.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }

    /**View initialization = Anim inflater + button init with the default attributes from the attrs.xml*/
    init {
        //set the button clickable
        isClickable=true

        //Animator inflation
        barAnimator = AnimatorInflater.loadAnimator(context,R.animator.progress_animator) as ValueAnimator
        barAnimator.addUpdateListener(barUpdateListener)
        circleAnimator = AnimatorInflater.loadAnimator(context,R.animator.circle_animator) as ValueAnimator
        circleAnimator.addUpdateListener(circleUpdateListener)

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultBackgroundColor = getColor(R.styleable.LoadingButton_defaultBackgroundColor, 0)
            progressBarColor = getColor(R.styleable.LoadingButton_progressBarColor, 0)
            borderColor = getColor(R.styleable.LoadingButton_borderColor,0)
            defaultText = getString(R.styleable.LoadingButton_defaultText)
            loadingText = getString(R.styleable.LoadingButton_loadingText)
            completedText = getString(R.styleable.LoadingButton_completedText)
            textColor = getColor(R.styleable.LoadingButton_textColor,0)
            defaultCircleColor = getColor(R.styleable.LoadingButton_defaultCircleColor, 0)
        }
    }

    /**Drawing section*/
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawText(canvas)
        drawCircle(canvas)
        drawBorder(canvas)
    }
            /**Draws the rectangle background */
            private fun drawBackground(canvas: Canvas) {
                paint.color = defaultBackgroundColor
                paint.style = Paint.Style.FILL
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                if (buttonState == ButtonState.Loading) {
                    paint.color = progressBarColor
                    canvas.drawRect(0f,0f,width*(barProgress/100).toFloat(),height.toFloat(),paint)
                }
            }
            /**Draws the text*/
            private fun drawText(canvas: Canvas) {
                paint.color = textColor
                paint.textAlign = Paint.Align.CENTER

                when (buttonState) {
                    ButtonState.Loading -> {
                        canvas.drawText(
                            loadingText ?: "",
                            (canvas.width / 2).toFloat(),
                            ((canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2)),
                            paint
                        )
                    }
                    ButtonState.Clickable -> {
                        canvas.drawText(
                            defaultText ?: "",
                            (canvas.width / 2).toFloat(),
                            ((canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2)),
                            paint
                        )
                    }
                    else -> {
                        canvas.drawText(
                            completedText ?: "",
                            (canvas.width / 2).toFloat(),
                            ((canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2)),
                            paint
                        )
                    }
                }
            }
            /**Draws the circle*/
            private fun drawCircle(canvas: Canvas) {
                paint.color = defaultCircleColor
                paint.style = Paint.Style.FILL
                paint.isAntiAlias = true
                if (buttonState == ButtonState.Loading) {
                canvas.drawArc(
                    RectF(
                        (width / 2) + 250.toFloat(),
                        (height / 2) - 50.toFloat(),
                        (width / 2) + 350.toFloat(),
                        (height / 2) + 50.toFloat()
                    ), 0f, circleProgress.toFloat(), true, paint
                )}
            }
            /**Draws the border of the rectangle*/
            //For some reason this method must be called AFTER the text method, otherwise it messes it all up
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

    fun animations() {
        barAnimator.start()
        circleAnimator.start()
        barAnimator.doOnEnd {
            buttonState = ButtonState.Completed
        }
    }


    fun setButtonToClickable() {
        buttonState = ButtonState.Clickable
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }





}