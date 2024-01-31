package com.small.uikit.ui.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.small.uikit.R

/**
 * Created by small-ho on 2023/6/19 星期一 16:28
 * title: 重定义移动控件
 */
class UiMoveLayout : ConstraintLayout {

    private var mBackgroundNormal = 0
    private var mCornerRadius = -1
    private var mCornerRadiusTopLeft = 0
    private var mCornerRadiusTopRight = 0
    private var mCornerRadiusBottomLeft = 0
    private var mCornerRadiusBottomRight = 0

    private var mBorderWidth = 0
    private var mBorderColor = 0

    private var mLastRawX: Float = 0F
    private var mLastRawY: Float = 0F
    private var isDrug = false
    private var mRootMeasuredWidth = 0
    private var mRootMeasuredHeight = 0
    private var mRootTopY = 0
    private var mCustomIsAttach = false
    private var mCustomIsDrag = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        isClickable = true
        initAttrs(context, attrs)
        initDrawableSet()
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        if (context == null || attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseView)
        //  背景
        mBackgroundNormal = typedArray.getColor(R.styleable.BaseView_background_normal, mBackgroundNormal)
        //  角度
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius, mCornerRadius)
        mCornerRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_top_left, mCornerRadiusTopLeft)
        mCornerRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_top_right, mCornerRadiusTopRight)
        mCornerRadiusBottomLeft = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_bottom_left, mCornerRadiusBottomLeft)
        mCornerRadiusBottomRight = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_bottom_right, mCornerRadiusBottomRight)
        //  边框
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.BaseView_border_width, mBorderWidth)
        mBorderColor = typedArray.getColor(R.styleable.BaseView_border_color, mBorderColor)
        //  吸附
        mCustomIsAttach = typedArray.getBoolean(R.styleable.BaseView_custom_is_Attach, false)
        //  拖曳
        mCustomIsDrag = typedArray.getBoolean(R.styleable.BaseView_custom_is_drag, false)
        typedArray.recycle()
    }

    private fun initDrawableSet() {
        val shape = GradientDrawable()
        //  颜色
        shape.setColor(mBackgroundNormal)
        //  角度
        if (mCustomIsAttach) {
            when(x) {
                0F -> shape.cornerRadii = floatArrayOf(
                    0F, 0F,
                    mCornerRadius.toFloat(), mCornerRadius.toFloat(),
                    mCornerRadius.toFloat(), mCornerRadius.toFloat(),
                    0F, 0F
                )
                width.toFloat() -> shape.cornerRadii = floatArrayOf(
                    mCornerRadius.toFloat(), mCornerRadius.toFloat(),
                    0F, 0F, 0F, 0F,
                    mCornerRadius.toFloat(), mCornerRadius.toFloat()
                )
            }
        }else {
            if (mCornerRadius > 0) {
                shape.cornerRadius = mCornerRadius.toFloat()
            } else {
                shape.cornerRadii = floatArrayOf(
                    mCornerRadiusTopLeft.toFloat(), mCornerRadiusTopLeft.toFloat(),
                    mCornerRadiusTopRight.toFloat(), mCornerRadiusTopRight.toFloat(),
                    mCornerRadiusBottomRight.toFloat(), mCornerRadiusBottomRight.toFloat(),
                    mCornerRadiusBottomLeft.toFloat(), mCornerRadiusBottomLeft.toFloat()
                )
            }
        }
        //  边框
        shape.setStroke(mBorderWidth, mBorderColor)
        this.background = shape
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            return if (mCustomIsDrag) {
                onTouchEvent(event)
                true
            }else {
                super.dispatchTouchEvent(event)
            }
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            //判断是否需要滑动
            if (mCustomIsDrag) {
                //当前手指的坐标
                val mRawX = it.rawX
                val mRawY = it.rawY
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {//手指按下
                        isDrug = false
                        //记录按下的位置
                        mLastRawX = mRawX
                        mLastRawY = mRawY
                        if (parent is ViewGroup) {
                            val mViewGroup = parent as ViewGroup
                            val location = IntArray(2)
                            mViewGroup.getLocationInWindow(location)
                            //获取父布局的高度
                            mRootMeasuredHeight = mViewGroup.measuredHeight
                            mRootMeasuredWidth = mViewGroup.measuredWidth
                            //获取父布局顶点的坐标
                            mRootTopY = location[1]
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {//手指滑动
                        if (mRawX >= 0 && mRawX <= mRootMeasuredWidth && mRawY >= mRootTopY && mRawY <= (mRootMeasuredHeight + mRootTopY)) {
                            //手指X轴滑动距离
                            val differenceValueX: Float = mRawX - mLastRawX
                            //手指Y轴滑动距离
                            val differenceValueY: Float = mRawY - mLastRawY
                            //判断是否为拖动操作
                            if (!isDrug) {
                                isDrug = kotlin.math.sqrt(((differenceValueX * differenceValueX) + (differenceValueY * differenceValueY)).toDouble()) >= 2
                            }
                            //获取手指按下的距离与控件本身X轴的距离
                            val ownX = x
                            //获取手指按下的距离与控件本身Y轴的距离
                            val ownY = y
                            //理论中X轴拖动的距离
                            var endX: Float = ownX + differenceValueX
                            //理论中Y轴拖动的距离
                            var endY: Float = ownY + differenceValueY
                            //X轴可以拖动的最大距离
                            val maxX: Float = mRootMeasuredWidth - width.toFloat()
                            //Y轴可以拖动的最大距离
                            val maxY: Float = mRootMeasuredHeight - height.toFloat()
                            //X轴边界限制
                            endX = if (endX < 0) 0F else (if (endX > maxX) maxX else endX)
                            //Y轴边界限制
                            endY = if (endY < 0) 0F else (if (endY > maxY) maxY else endY)
                            //开始移动
                            x = endX
                            y = endY
                            //记录位置
                            mLastRawX = mRawX
                            mLastRawY = mRawY
                        }
                    }

                    MotionEvent.ACTION_UP -> {//手指离开
                        if (mCustomIsAttach) {
                            //判断是否为点击事件
                            if (isDrug) {
                                val center = mRootMeasuredWidth / 2
                                //自动贴边
                                if (mLastRawX <= center) {
                                    //向左贴边
                                    animate().setInterpolator(BounceInterpolator()).setDuration(500).x(0F).start()

                                    val shape = GradientDrawable()
                                    //  颜色
                                    shape.setColor(mBackgroundNormal)
                                    //  角度
                                    shape.cornerRadii = floatArrayOf(
                                        0F, 0F,
                                        mCornerRadius.toFloat(), mCornerRadius.toFloat(),
                                        mCornerRadius.toFloat(), mCornerRadius.toFloat(),
                                        0F, 0F
                                    )
                                    //  边框
                                    shape.setStroke(mBorderWidth, mBorderColor)
                                    this.background = shape
                                } else {
                                    //向右贴边
                                    animate().setInterpolator(BounceInterpolator()).setDuration(500).x(mRootMeasuredWidth - width.toFloat()).start()

                                    val shape = GradientDrawable()
                                    //  颜色
                                    shape.setColor(mBackgroundNormal)
                                    //  角度
                                    shape.cornerRadii = floatArrayOf(
                                        mCornerRadius.toFloat(), mCornerRadius.toFloat(),
                                        0F, 0F, 0F, 0F,
                                        mCornerRadius.toFloat(), mCornerRadius.toFloat()
                                    )
                                    //  边框
                                    shape.setStroke(mBorderWidth, mBorderColor)
                                    this.background = shape
                                }
                            }
                        }
                    }
                }
            }
        }
        //是否拦截事件
        return if (isDrug) isDrug else super.onTouchEvent(event)
    }
}