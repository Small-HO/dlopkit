package com.small.uikit.ui.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.small.uikit.R

/**
 * Created by small-ho on 2022/06 17:57
 * title: 重定义textView样式
 */
class UiTextView : AppCompatTextView {

    private var mBackgroundNormal = 0
    private var mCornerRadius = -1
    private var mCornerRadiusTopLeft = 0
    private var mCornerRadiusTopRight = 0
    private var mCornerRadiusBottomLeft = 0
    private var mCornerRadiusBottomRight = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributeSet(context, attrs)
        initDrawableSet()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UiTextView)
            mBackgroundNormal = typedArray.getColor(R.styleable.UiTextView_background_normal, mBackgroundNormal)
            mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.UiTextView_corner_radius, mCornerRadius)
            mCornerRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.UiTextView_corner_radius_top_left, mCornerRadiusTopLeft)
            mCornerRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.UiTextView_corner_radius_top_right, mCornerRadiusTopRight)
            mCornerRadiusBottomLeft = typedArray.getDimensionPixelSize(R.styleable.UiTextView_corner_radius_bottom_left, mCornerRadiusBottomLeft)
            mCornerRadiusBottomRight = typedArray.getDimensionPixelSize(R.styleable.UiTextView_corner_radius_bottom_right, mCornerRadiusBottomRight)
            typedArray.recycle()
        }
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
        this.background = gradientDrawable
    }

}