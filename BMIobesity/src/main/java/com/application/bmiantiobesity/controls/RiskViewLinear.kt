package com.application.bmiantiobesity.controls

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.application.bmiantiobesity.R


class RiskViewLinear : View {

    /*companion object {
        private val TAG = "BoxDrawingView"
    }*/

    //private var mCurrentPoint: PointF? = null
    //private var isClick = false

    private val mPaint = Paint()
    private val mTextPaint = Paint()
    private var startColor = Color.GREEN
    private var endColor = Color.RED
    //private lateinit var mBackgroundPaint: Paint

    private var percent:Int = 0
    private val correctionViewDraw = 4f

    // Используется при создании представления в коде
    constructor(context: Context) : super(context, null) {
        init()
    }

    // Используется при заполнении представления по разметке XML
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // Установка значений из XML
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RiskViewLinear)

        percent = attributes.getInt(R.styleable.RiskViewLinear_percent, 50)
        if (percent > 100) percent = 100
        if (percent < 0) percent = 0

        startColor = attributes.getInt(R.styleable.RiskViewLinear_line_start_color, Color.GREEN)
        endColor = attributes.getInt(R.styleable.RiskViewLinear_line_end_color, Color.RED)
        //mBackgroundPaint.color = attributes.getColor(R.styleable.RiskView_background_color, Color.WHITE)

        attributes.recycle()

        init()
    }

    private fun init() {
        // Aон закрашивается серовато-белым цветом
        //mBackgroundPaint = Paint()
        //mBackgroundPaint.color = -0x71020

        mTextPaint.color = Color.BLACK
        mTextPaint.textSize = 20f

        // Фон закрашивается по процентам
        percent = 65
    }


    // Программная установка процентов
    fun setPercent(percent:Int){
        when {
            percent > 100 -> this.percent = 100
            percent < 0 -> this.percent = 0
            else -> this.percent = percent
        }

        invalidate()
        requestLayout()
    }

    // Программная установка цвета
    fun setStringColor(color: String){
        var resultColor = color.substringAfter('#')

        if (resultColor.length > 6) resultColor = resultColor.substring(2, resultColor.length)

        startColor = Color.parseColor("#4F$resultColor")
        endColor = Color.parseColor("#0F$resultColor")

        invalidate()
        requestLayout()
    }

    /*fun setLineColor(color: Int){
        mPaint.color = color

        invalidate()
        requestLayout()
    }*/

    override fun onDraw(canvas: Canvas) {

        // Создание градиента (работает только здесь)
        mPaint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), startColor, endColor, Shader.TileMode.MIRROR)

        // Заполнение фона
        //canvas.drawPaint(mBackgroundPaint)
        //val str = "Width = $width Height = $height"


        // 3 основных линии
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), mPaint)
        canvas.drawLine((width/2).toFloat(), 0f, (width/2).toFloat(), height.toFloat(), mPaint)
        canvas.drawLine(width.toFloat() - 1f, 0f, width.toFloat() - 1f, height.toFloat(), mPaint)

        // Заполнение процентов
        canvas.drawRect(0f,
            0f + correctionViewDraw,
            (width * percent / 100).toFloat(),
            height.toFloat() - correctionViewDraw,
             mPaint)

        // Текст процентов
        val x = width / 2 - width * 0.05f
        val y = height / 2 + height * 0.22f
        canvas.drawText("$percent %", x, y, mTextPaint)
    }

    //super.onDraw(canvas);
    //canvas.restore();

    /*override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                // Сброс текущего состояния
                isClick = true
                invalidate()

                //TO DO  Сюда реализовать update() наблюдателей
                //Log.i(TAG,"Send message!")
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"

                //mCurrentPoint = current
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"

                isClick = false
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
            }
        }
        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y)
        return true
    }*/
}