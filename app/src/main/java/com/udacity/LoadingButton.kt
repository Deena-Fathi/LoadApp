package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlinx.coroutines.delay
import kotlin.properties.Delegates

/**
 *  LoadingButton is custom view class that extends the View class in Android.
 *  The LoadingButton has four states (ButtonState) that define its appearance and behavior:
 *  Clicked, Loading, Completed, and Normal. Each time the button changes state, it redraws its view.
 *
 *  In the constructor, the LoadingButton reads attributes from a custom styleable LoadingButton,
 *  which can be defined in XML using the <declare-styleable> tag. This allows the LoadingButton
 *  to be customized with different colors.
 */
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize: Int = 0
    private var heightSize: Int = 0
    private var progress: Int = 0

    private var progressArc: RectF = RectF()
    private var buttonText: String = resources.getString(R.string.button_name)

    private var buttonBackgroundColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var buttonLoadingColor: Int = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var buttonCircleColor: Int = ContextCompat.getColor(context, R.color.colorAccent)
    private var buttonTextColor: Int = ContextCompat.getColor(context, R.color.white)

    private var valueAnimator: ValueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000)

    /**
     * When the LoadingButton changes state, its buttonState variable is updated using the
     * Delegates.observable() method. Depending on the new state, different actions are performed,
     * such as changing the button text, starting or stopping a ValueAnimator, or setting the
     * enabled state of the button. In each case, the invalidate() method is called to request
     * a redraw of the button.
     */
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
                isEnabled = false
            }
            ButtonState.Loading -> {
                valueAnimator = ValueAnimator.ofInt(0, 2000).apply {
                    addUpdateListener {
                        progress = animatedValue as Int
                        invalidate()
                    }
                    duration = 2000
                    doOnStart {
                        buttonText = resources.getString(R.string.button_loading)
                        isEnabled = false
                        repeatMode = ValueAnimator.REVERSE
                        repeatCount = ValueAnimator.INFINITE
                    }
                    doOnEnd {
                        progress = 0
                        isEnabled = true
                    }
                    start()
                }
                isEnabled = false
            }
            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.downloaded)
                progress = 0
                isEnabled = false
            }
            ButtonState.Normal -> {
                buttonText = resources.getString(R.string.button_name)
                isEnabled = true
            }
        }

        invalidate()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonLoadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            buttonCircleColor = getColor(R.styleable.LoadingButton_buttonCircleColor, 0)
        }
    }

    /**
     * In the onDraw() method, the LoadingButton draws the different components of the button,
     * such as the background, loading progress, progress circle, and text,
     * using the Canvas object and the Paint object.
     */

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the button background
        paint.color = buttonBackgroundColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        // Draw the loading progress
        paint.color = buttonLoadingColor
        canvas.drawRect(0f, 0f, widthSize * progress / 2000f, heightSize.toFloat(), paint)

        // Draw the progress circle
        val circleAngle = progress / 2000f * 360f
        paint.color = buttonCircleColor
        canvas.drawArc(progressArc, 0f, circleAngle, true, paint)

        // Draw the text
        paint.color = buttonTextColor
        paint.getTextBounds(buttonText, 0, buttonText.length, Rect())
        canvas.drawText(
            buttonText,
            widthSize / 2f,
            measuredHeight.toFloat() / 2 - Rect().centerY(),
            paint
        )
    }

    /**
     * In the onMeasure() method, the LoadingButton calculates its minimum width and height,
     * and sets its measured dimensions using setMeasuredDimension().
     */
    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
        progressArc = RectF(
            widthSize - 250f,
            heightSize / 2 - 25f,
            widthSize.toFloat() - 200f,
            heightSize / 2 + 25f
        )
    }

    /**
     * Finally, the LoadingButton provides a public method called downloadCompleted(),
     * which ends the loading animation and sets the button to the Completed state.
     * After a delay of two seconds, the button returns to its Normal state.
     */
    fun downloadCompleted() {
        valueAnimator.end()
        buttonState = ButtonState.Completed
        Handler(Looper.getMainLooper()).postDelayed({
            buttonState = ButtonState.Normal
        }, 2000)
    }

}