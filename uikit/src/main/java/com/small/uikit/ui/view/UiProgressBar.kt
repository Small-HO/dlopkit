package com.small.uikit.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.small.uikit.R
import kotlin.math.ceil

/**
 * Created by small-ho on 2022/08 17:09
 * title: 自定义ProgressBar
 */
class UiProgressBar : View {

    private var mStyle = 0          // 控件类型 0：圆形 1：条形

    private var mCornerRadius = -1  // 角度

    private var mStrokeWidth = 6    // 画笔进度宽度
    private var mBarBgColor = 0     // 进度条背景色
    private var mBarColor = 0       // 进度条填充色
    private var mTextColor = 0      // 文本颜色
    private var mTextSize = 0       // 文本大小
    private var mBarMax = 360       // 最大进度
    private var mBarNum = 0         // 进度

    private var mPaintBack: Paint? = null   // 背景色画笔
    private var mPaint: Paint? = null       // 进度画笔
    private var mPaintText: Paint? = null   // 文本画笔

    private var rectFBg: RectF? = null       // 背景
    private var rectFProgress: RectF? = null // 进度背景

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributeSet(context, attrs)
        when(mStyle) {
            0 -> initPaintSet()
            1 -> initRectFSet()
        }
    }

    private fun initAttributeSet(context: Context?, attrs: AttributeSet?) {
        if (context == null || attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UiProgressBar)
        // 类型
        mStyle = typedArray.getInteger(R.styleable.UiProgressBar_bar_style, mStyle)
        // 角度
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.UiProgressBar_corner_radius, mCornerRadius)
        // 进度条
        mStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.UiProgressBar_bar_width, mStrokeWidth)
        mBarBgColor = typedArray.getColor(R.styleable.UiProgressBar_bar_background, mBarBgColor)
        mBarColor = typedArray.getColor(R.styleable.UiProgressBar_bar_color, mBarColor)
        // 进度
        mBarMax = typedArray.getInteger(R.styleable.UiProgressBar_bar_max, mBarMax)
        mBarNum = typedArray.getInteger(R.styleable.UiProgressBar_bar_num, mBarNum)
        // 文本
        mTextColor = typedArray.getColor(R.styleable.UiProgressBar_bar_text_color, mTextColor)
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.UiProgressBar_bar_text_size, mTextSize)
        typedArray.recycle()
    }

    /** 圆形进度条 */
    private fun initPaintSet() {
        mPaintBack = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintBack?.let {
            it.isAntiAlias = true
            it.color = mBarBgColor
            it.strokeWidth = mStrokeWidth.toFloat()
            it.style = Paint.Style.STROKE
        }
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.let {
            it.isAntiAlias = true
            it.color = mBarColor
            it.strokeWidth = mStrokeWidth.toFloat()
            it.style = Paint.Style.STROKE
            it.strokeCap = Paint.Cap.ROUND
        }
        if (mTextSize != 0) {
            mPaintText = Paint()
            mPaintText?.let {
                it.isAntiAlias = true
                it.color = mTextColor
                it.textSize = sp2px(mTextSize).toFloat()
                it.style = Paint.Style.STROKE
            }
        }
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
        // 绘制文字
        drawText(canvas)
    }

    private fun drawBack(canvas: Canvas?) {
        when (mStyle) {
            0 -> {
                val rectF = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), (width - mStrokeWidth).toFloat(), (height - mStrokeWidth).toFloat())
                mPaintBack?.let { canvas?.drawArc(rectF, -90F, 360F, false, it) }
            }
            1 -> {
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
        }
    }

    private fun drawProgress(canvas: Canvas?) {
        when (mStyle) {
            0 -> {
                val rectF = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), (width - mStrokeWidth).toFloat(), (height - mStrokeWidth).toFloat())
                mPaint?.let { canvas?.drawArc(rectF, -90F, mBarNum.toFloat() / mBarMax.toFloat() * 360F, false, it) }
            }
            1 -> {
                mPaint?.shader = null
                mPaint?.color = mBarColor
                rectFProgress?.right = width.toFloat() * (mBarNum.toFloat() / mBarMax.toFloat())
                if (height != 0) {
                    val top = (height / 2).toFloat() - (mStrokeWidth / 2).toFloat()
                    if (top <= 0) {
                        rectFProgress?.top = 0F
                    }else {
                        rectFProgress?.top = top
                    }
                }
                val bottom = (height / 2).toFloat() + (mStrokeWidth / 2).toFloat()
                if (bottom >= height) {
                    rectFProgress?.bottom = height.toFloat()
                }else {
                    rectFProgress?.bottom = bottom
                }
                if (mCornerRadius > 0) {
                    mPaint?.let { rectFProgress?.let { it1 -> canvas?.drawRoundRect(it1, mCornerRadius.toFloat(), mCornerRadius.toFloat(), it) } }
                }else {
                    mPaint?.let { rectFProgress?.let { it1 -> canvas?.drawRoundRect(it1, 0F, 0F, it) } }
                }
            }
        }
    }

    private fun drawText(canvas: Canvas?) {
        val percent = mBarNum.toFloat() / mBarMax.toFloat() * 360F
        val x = width / 2 - getTextWidth(percent) / 2
        val y = height / 2 + getTextHeight() / 4
        mPaintText?.let { canvas?.drawText("$mBarNum/$mBarMax", x, y, it) }
    }

    private fun getTextWidth(percent: Float): Float {
        val mTxtWidth: Float
        val text = "$mBarMax/$mBarMax"
        val defaultText = "0/$mBarMax"
        val defaultText2 = "$mBarNum/$mBarMax"
        mTxtWidth = when (percent) {
            0F -> mPaintText?.measureText(defaultText, 0, defaultText.length) ?: 0F
            in 1F..360F -> mPaintText?.measureText(defaultText2, 0, defaultText2.length) ?: 0F
            else -> mPaintText?.measureText(text, 0, text.length) ?: 0F
        }
        return mTxtWidth
    }

    private fun getTextHeight(): Float {
        val fm = mPaintText?.fontMetrics
        fm?.let { return ceil((it.descent - it.ascent).toDouble()).toFloat() } ?: kotlin.run { return 0F }
    }

    private fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), resources.displayMetrics).toInt()
    }

    /** 设置最大阈值 */
    fun setBarMax(max: Int) {
        this.mBarMax = max
        requestLayout()
        invalidate()
    }

    /** 设置进度 */
    fun setBarNum(num: Int) {
        this.mBarNum = num
        requestLayout()
        invalidate()
    }
}