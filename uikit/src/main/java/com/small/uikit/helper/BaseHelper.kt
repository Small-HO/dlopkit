package com.small.uikit.helper

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import com.small.uikit.R


/**
 * Created by small-ho on 2022/06 15:05
 * title: 基础帮助类
 */
class BaseHelper<T : View>(context: Context, private val view: T, attrs: AttributeSet?) : BaseHelperImpl {

    private var mBackgroundNormal = 0
    private var mCornerRadius = -1
    private var mCornerRadiusTopLeft = 0
    private var mCornerRadiusTopRight = 0
    private var mCornerRadiusBottomLeft = 0
    private var mCornerRadiusBottomRight = 0

    private var mBorderWidth = 0
    private var mBorderColor = 0

    init {
        initAttributeSet(context, attrs)
        initDrawableSet()
    }

    private fun initAttributeSet(context: Context?, attrs: AttributeSet?) {
        if (context == null || attrs == null) {
            return
        }
        val typedArray = view.context.obtainStyledAttributes(attrs, R.styleable.BaseView)
        mBackgroundNormal = typedArray.getColor(R.styleable.BaseView_background_normal, mBackgroundNormal)
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius, mCornerRadius)
        mCornerRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_top_left, mCornerRadiusTopLeft)
        mCornerRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_top_right, mCornerRadiusTopRight)
        mCornerRadiusBottomLeft = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_bottom_left, mCornerRadiusBottomLeft)
        mCornerRadiusBottomRight = typedArray.getDimensionPixelSize(R.styleable.BaseView_corner_radius_bottom_right, mCornerRadiusBottomRight)

        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.BaseView_border_width, mBorderWidth)
        mBorderColor = typedArray.getColor(R.styleable.BaseView_border_color, mBorderColor)
        typedArray.recycle()
    }

    private fun initDrawableSet() {
        val shape = GradientDrawable()
        //  颜色
        shape.setColor(mBackgroundNormal)
        //  角度
        if (mCornerRadius > 0) {
            shape.cornerRadius = mCornerRadius.toFloat()
        } else {
            shape.cornerRadii = floatArrayOf(
                mCornerRadiusTopLeft.toFloat(), mCornerRadiusTopLeft.toFloat(),
                mCornerRadiusTopRight.toFloat(), mCornerRadiusTopRight.toFloat(),
                mCornerRadiusBottomLeft.toFloat(), mCornerRadiusBottomLeft.toFloat(),
                mCornerRadiusBottomRight.toFloat(), mCornerRadiusBottomRight.toFloat()
            )
        }
        //  边框
        shape.setStroke(mBorderWidth, mBorderColor)
        view.background = shape
    }

}