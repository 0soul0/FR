package com.gj.fr

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.gj.arcoredraw.R
import kotlin.properties.Delegates


class FrView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {


    private var viewWith by Delegates.notNull<Float>()
    private var viewHeight by Delegates.notNull<Float>()
    private var unit by Delegates.notNull<Float>()
    private var circleCenterX by Delegates.notNull<Float>()
    private var circleCenterY by Delegates.notNull<Float>()
    private var paint: Paint = Paint()
    private var rotate by Delegates.notNull<Float>()
    private var value = 300f
    private var screwCount = 8
    private var text:String="DN125|JIS16K|JISB221"


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        viewWith = w.toFloat()

        val minh: Int = MeasureSpec.getSize(w) - rootView.width + paddingBottom + paddingTop
        val h: Int = resolveSizeAndState(minh, heightMeasureSpec, 0)
        viewHeight = h.toFloat()
        setMeasuredDimension(w, h)
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        animator()
    }

    private fun animator() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.apply {
            duration = 500
            repeatMode = ValueAnimator.RESTART
//            repeatCount = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener {
                value = (it.animatedValue as Float) * 360f
                postInvalidate()
            }
            start()
        }
    }


    fun setScrewCount(screwCount: Int) {
        this.screwCount = screwCount
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        unit = viewWith / 24
        circleCenterX = viewWith / 2
        circleCenterY = viewHeight / 2
        drawCircle(canvas)

        drawText(canvas)
    }

    private fun drawText(canvas: Canvas?){
        if (value < 350) return

        canvas?.drawText(text, unit, 2*circleCenterX+3*unit, paint.apply {
            textSize = 50f
            style=Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth=0f
        })
    }

    private fun drawCircle(canvas: Canvas?) {

        paint.apply {
            color = resources.getColor(R.color.black)
            style = Paint.Style.STROKE
            strokeWidth = 25f
        }

        val paint2 = Paint().apply {
            color = resources.getColor(R.color.white)
            style = Paint.Style.FILL
        }

        val oval = RectF(unit, unit, 2 * circleCenterX - unit, 2 * circleCenterX - unit)
        canvas?.drawArc(oval, 0f, value, true, paint)
        canvas?.drawArc(oval, 0f, 360f, true, paint2)

        val smallUnit = 7 * unit
        val ovalSmall = RectF(
            smallUnit,
            smallUnit,
            2 * circleCenterX - smallUnit,
            2 * circleCenterX - smallUnit
        )
        canvas?.drawArc(ovalSmall, 0f, value, true, paint)
        canvas?.drawArc(ovalSmall, 0f, 360f, true, paint2)


        if (value < 350) return

        paint.strokeWidth = 9f
        val len = (smallUnit + unit) / 3
        val radius = (smallUnit - unit) / 6
        rotate = 360f / screwCount
        canvas?.save()
        for (i in 0 until screwCount) {
            canvas?.rotate(rotate, circleCenterX, circleCenterX)
            canvas?.drawCircle(len, circleCenterX, radius, paint)
        }
        canvas?.restore()


    }

}