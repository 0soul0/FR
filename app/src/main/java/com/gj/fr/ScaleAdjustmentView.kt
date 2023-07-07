package com.gj.fr

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.gj.arcoredraw.R
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.properties.Delegates

class ScaleAdjustmentView(context: Context?, attrs: AttributeSet?) : View(context, attrs){


    private var viewWith by Delegates.notNull<Float>()
    private var viewHeight by Delegates.notNull<Float>()
    private var unit by Delegates.notNull<Float>()
    private var circleCenterX by Delegates.notNull<Float>()
    private var circleCenterY by Delegates.notNull<Float>()
    private var radius by Delegates.notNull<Float>()
    private var currentValue = 0f
    private var paint: Paint = Paint()
    private var rotate by Delegates.notNull<Float>()
    private var numberRage = listOf(10f, 500f)
    private var value= 300f

    private var dx = 0f
    private var dy = 0f
    private lateinit var valueListener:(value:Float)->Unit
    private var isTouch=false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        viewWith = w.toFloat()

        val minh: Int = MeasureSpec.getSize(w) - rootView.width + paddingBottom + paddingTop
        val h: Int = resolveSizeAndState(minh, heightMeasureSpec, 0)
        viewHeight = h.toFloat()
        setMeasuredDimension(w, h)
    }


    fun setValue(value: Float) {
        this.value = value
        invalidate()
    }
    fun getValue() = value

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        unit = viewHeight / 12
        circleCenterX = viewWith + unit * 2
        circleCenterY = viewHeight / 2
        radius = viewHeight / 2 + unit / 2
        drawCircle(canvas)

        drawText(canvas)
    }



    private fun drawText(canvas: Canvas?) {

        paint.apply {
            color = Color.BLACK
            style = Paint.Style.FILL_AND_STROKE
            textSize = 55f
            isAntiAlias = true
        }

        rotate = 360f / ((numberRage[1] + 10 - numberRage[0]) / 10f)
        var startRotate = rotate * ((value - 10) / 10);
        canvas?.save()
        canvas?.rotate(-startRotate, circleCenterX, circleCenterY)
        for (i in numberRage[0].toInt()..numberRage[1].toInt() step 10) {

            canvas?.let {
                it.drawText(i.toString(), circleCenterX - radius + 20, circleCenterY, paint)
                it.drawRect(
                    circleCenterX - radius + 2 * unit,
                    circleCenterY - 28,
                    circleCenterX - radius + 4 * unit,
                    circleCenterY - 18,
                    paint
                )

                it.rotate(rotate, circleCenterX, circleCenterY)

            }
        }
        canvas?.restore()
    }

    private fun drawCircle(canvas: Canvas?) {

        paint.apply {
            color = resources.getColor(R.color.gray_t)
            style = Paint.Style.FILL_AND_STROKE
        }
        canvas?.drawCircle(circleCenterX, circleCenterY, radius, paint)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(!isTouch)return false
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                dx=event.x
                dy=event.y
            }
            MotionEvent.ACTION_MOVE->{
                value += (event.y-dy)/10
                valueListener(twoDigits(value))
                dy=event.y
                invalidate()
            }
        }
        return true
    }

    fun twoDigits(number:Float):Float{
        val format = DecimalFormat("#.##")
        format.roundingMode= RoundingMode.FLOOR
        return format.format(number).toFloat()
    }

    fun setIsTouch(isTouch:Boolean){
        this.isTouch=isTouch
    }

    fun setValueChangeListener(lister:(value:Float)->Unit){
        valueListener=lister
    }
}