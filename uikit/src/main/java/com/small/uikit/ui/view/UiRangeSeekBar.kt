package com.small.uikit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.small.uikit.R

/**
 * Created by small-ho on 2024/3/12 11:51
 * title: 自定义两端拖动进度条（选取范围）
 */
class UiRangeSeekBar : View {

    private var mCornerRadius = -1  // 角度

    private var mStrokeWidth = 6    // 画笔进度宽度
    private var mBarBgColor = 0     // 进度条背景色
    private var mBarColor = 0       // 进度条填充色

    private var mPaintBack: Paint? = null   // 背景色画笔
    private var mPaint: Paint? = null       // 进度画笔

    private var rectFBg: RectF? = null       // 背景
    private var rectFProgress: RectF? = null // 进度背景

    private var duration = 0
    private var leftThumbX = 0f
    private var rightThumbX = 0f

    private var mSeekBarListener: ((Int, Int) -> Unit)? = null
    fun onSeekBarListener(listener: (Int, Int) -> Unit) { this.mSeekBarListener = listener }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributeSet(context, attrs)
        initRectFSet()
    }

    private fun initAttributeSet(context: Context?, attrs: AttributeSet?) {
        if (context == null || attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UiRangeSeekBar)
        // 角度
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.UiRangeSeekBar_corner_radius, mCornerRadius)
        // 进度条
        mStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.UiRangeSeekBar_bar_width, mStrokeWidth)
        mBarBgColor = typedArray.getColor(R.styleable.UiRangeSeekBar_bar_background, mBarBgColor)
        mBarColor = typedArray.getColor(R.styleable.UiRangeSeekBar_bar_color, mBarColor)
        typedArray.recycle()
    }

    /** 条形进度条 */
    private fun initRectFSet() {
        rectFBg = RectF(0F, 0F, 0F, height.toFloat())
        rectFProgress = RectF(0F, 0F, 0F, height.toFloat())
        mPaint = Paint()
        mPaintBack = Paint()
        //设置抗锯齿
        mPaint?.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制背景
        drawBack(canvas)
        // 绘制进度
        drawProgress(canvas)
    }

    private fun drawBack(canvas: Canvas?) {
        if (mBarBgColor == 0) { return }
        mPaintBack?.shader = null
        mPaintBack?.color = mBarBgColor
        rectFBg?.right = width.toFloat()
        if (height != 0) {
            val top = (height / 2).toFloat() - (mStrokeWidth / 2).toFloat()
            if (top <= 0) {
                rectFBg?.top = 0F
            }else {
                rectFBg?.top = top
            }
            val bottom = (height / 2).toFloat() + (mStrokeWidth / 2).toFloat()
            if (bottom >= height) {
                rectFBg?.bottom = height.toFloat()
            }else {
                rectFBg?.bottom = bottom
            }
        }
        if (mCornerRadius > 0) {
            mPaintBack?.let { rectFBg?.let { it1 -> canvas?.drawRoundRect(it1, mCornerRadius.toFloat(), mCornerRadius.toFloat(), it) } }
        }else {
            mPaintBack?.let { rectFBg?.let { it1 -> canvas?.drawRoundRect(it1, 0F, 0F, it) } }
        }
    }

    private fun drawProgress(canvas: Canvas?) {
        mPaint?.shader = null
        mPaint?.color = mBarColor
        if (leftThumbX == 0F && rightThumbX == 0F) {
            leftThumbX = paddingLeft.toFloat() + mCornerRadius
            rightThumbX = width.toFloat() - paddingRight.toFloat() - mCornerRadius
        }
        if (height != 0) {
            rectFProgress?.left = leftThumbX
            rectFProgress?.right = rightThumbX
            val top = (height / 2).toFloat() - (mStrokeWidth / 2).toFloat()
            if (top <= 0) {
                rectFProgress?.top = 0F
            }else {
                rectFProgress?.top = top
            }
            val bottom = (height / 2).toFloat() + (mStrokeWidth / 2).toFloat()
            if (bottom >= height) {
                rectFProgress?.bottom = height.toFloat()
            }else {
                rectFProgress?.bottom = bottom
            }
        }
        if (mCornerRadius > 0) {
            mPaint?.let { canvas?.drawCircle(leftThumbX, height / 2f, mCornerRadius.toFloat(), it) }
            mPaint?.let { rectFProgress?.let { it1 -> canvas?.drawRoundRect(it1, mCornerRadius.toFloat(), mCornerRadius.toFloat(), it) } }
            mPaint?.let { canvas?.drawCircle(if (rightThumbX == 0f) width.toFloat() - 15f else rightThumbX, height / 2f, mCornerRadius.toFloat(), it) }
        }else {
            mPaint?.let { canvas?.drawCircle(leftThumbX, height / 2f, 0F, it) }
            mPaint?.let { rectFProgress?.let { it1 -> canvas?.drawRoundRect(it1, 0F, 0F, it) } }
            mPaint?.let { canvas?.drawCircle(if (rightThumbX == 0f) width.toFloat() - 15f else rightThumbX, height / 2f, mCornerRadius.toFloat(), it) }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (leftThumbX == 0F && rightThumbX == 0F) { rightThumbX = width.toFloat() }
        val max = maxOf(leftThumbX, rightThumbX)
        val min = minOf(leftThumbX, rightThumbX)
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                //  防止超出边界
                val minThumbX = paddingLeft.toFloat() + mCornerRadius
                val maxThumbX = width.toFloat() - paddingRight.toFloat() - mCornerRadius
                try {
                    if (event.x <= (max - min) / 2 + min) {
                        leftThumbX = event.x.coerceIn(minThumbX, rightThumbX)
                    }else {
                        rightThumbX = event.x.coerceIn(leftThumbX, maxThumbX)
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
                //  时间转化
                val startSeconds = ((leftThumbX / (width - paddingLeft - paddingRight)) * duration).toInt()
                val endSeconds = ((rightThumbX / (width - paddingLeft - paddingRight)) * duration).toInt()
                mSeekBarListener?.let { it(startSeconds, endSeconds) }
                invalidate()
                return true
            }
        }
        return true
    }

    /** 设置最大阈值 */
    fun setBarMax(duration: Int) {
        this.duration = duration
        invalidate()
    }
}