package com.application.bmiantiobesity.controls

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.application.bmiantiobesity.R

class RiskViewHeight : View {

    private var startColor: Int = 0
    private var endColor: Int = 0

    //private lateinit var mBackgroundPaint: Paint
    private lateinit var mPaint: Paint

    private var percent:Float = 0f

    // Используется при создании представления в коде
    constructor(context: Context) : super(context, null) {
        init()
    }

    // Используется при заполнении представления по разметке XML
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // Установка значений из XML
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RiskViewHeight)

        percent = attributes.getFloat(R.styleable.RiskViewHeight_set_percent, 50f)


        //if (percent > 100f) percent = 100f
        if (percent < 0f) percent = 0f

        //mBackgroundPaint.color = attributes.getColor(R.styleable.RiskView_background_color, Color.WHITE)

        attributes.recycle()

        init()
    }

    private fun init() {
        // Фон закрашивается серовато-белым цветом
        //mBackgroundPaint = Paint()
        //mBackgroundPaint.color = Color.parseColor("#00FFFFFF")

        mPaint = Paint()
    }


    // Программная установка процентов
    fun setPercent(percent:Float){
        when {
            //percent > 100f -> this.percent = 100f
            percent < 0f -> this.percent = 0f
            else -> this.percent = percent
        }

        when {
            percent == -1f -> {
                startColor = Color.parseColor("#4FFFFFFF")
                endColor = Color.parseColor("#0FFFFFFF") }
            percent < 15f ->{
                startColor = Color.parseColor("#4F2BCF43")
                endColor = Color.parseColor("#0F2BCF43") }
            percent < 61f ->{
                startColor = Color.parseColor("#4FFF9500")
                endColor = Color.parseColor("#0FFF9500") }
            else -> {
                startColor = Color.parseColor("#4FFF2D55")
                endColor = Color.parseColor("#0FFF2D55") }
        }

        // Создание градиента (не работает )
        //mPaint.shader = LinearGradient(0f, height.toFloat(),0f, 0f,   startColor, endColor, Shader.TileMode.MIRROR)

        invalidate()
        requestLayout()
    }

    // Програмная установка цвета
    fun setStringColor(color: String){
        if (color.contains('#') and (color.length > 6)) {
            var resultColor = color.substringAfter('#')

            if (resultColor.length > 6) resultColor = resultColor.substring(2, resultColor.length)

            startColor = Color.parseColor("#4F$resultColor")
            endColor = Color.parseColor("#0F$resultColor")

            invalidate()
            requestLayout()
        }
    }

    /*fun setLineColor(color: Int){
        mPaint.color = color

        invalidate()
        requestLayout()
    }*/

    override fun onDraw(canvas: Canvas) {

        // Создание градиента (работает только здесь)
        mPaint.shader = LinearGradient(0f, height.toFloat(),0f, 0f,   startColor, endColor, Shader.TileMode.MIRROR)

        // Заполнение фона
        canvas.drawRect(0f, 0f , width.toFloat(), height.toFloat() , mPaint)
        //canvas.drawPaint(mBackgroundPaint)
        //val str = "Width = $width Height = $height"


        // 3 основных линии
        //canvas.drawLine(0f, 0f, 0f, height.toFloat(), mPaint)
        //canvas.drawLine((width/2).toFloat(), 0f, (width/2).toFloat(), height.toFloat(), mPaint)
        //canvas.drawLine(width.toFloat() - 1f, 0f, width.toFloat() - 1f, height.toFloat(), mPaint)

        // Заполнение процентов
        //canvas.drawRect(0f, 0f + correctionViewDraw, (width * percent / 100).toFloat(), height.toFloat() - correctionViewDraw, mPaint)

        // Текст процентов
        //val x = width / 2 - width * 0.05f
        //val y = height / 2 + height * 0.22f
        //canvas.drawText("$percent %", x, y, mTextPaint)
    }

}