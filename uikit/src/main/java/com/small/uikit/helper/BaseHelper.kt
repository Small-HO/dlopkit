package com.small.uikit.helper

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.small.uikit.R


/**
 * Created by small-ho on 2022/06 15:05
 * title: 基础帮助类
 */
class BaseHelper<T : View>(context: Context, private val view: T, attrs: AttributeSet?) {

    private var mBackgroundNormal = 0
    private var mCornerRadius = -1
    private var mCornerRadiusTopLeft = 0
    private var mCornerRadiusTopRight = 0
    private var mCornerRadiusBottomLeft = 0
    private var mCornerRadiusBottomRight = 0

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
        typedArray.recycle()
    }

    private fun initDrawableSet() {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(mBackgroundNormal)
        if (mCornerRadius > 0) {
            gradientDrawable.cornerRadius = mCornerRadius.toFloat()
        } else {
            gradientDrawable.cornerRadii = floatArrayOf(
                mCornerRadiusTopLeft.toFloat(), mCornerRadiusTopLeft.toFloat(),
                mCornerRadiusTopRight.toFloat(), mCornerRadiusTopRight.toFloat(),
                mCornerRadiusBottomLeft.toFloat(), mCornerRadiusBottomLeft.toFloat(),
                mCornerRadiusBottomRight.toFloat(), mCornerRadiusBottomRight.toFloat()
            )
        }
        view.background = gradientDrawable
    }

}