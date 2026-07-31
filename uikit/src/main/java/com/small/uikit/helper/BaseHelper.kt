package com.small.uikit.helper

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes
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
        view.context.withStyledAttributes(attrs, R.styleable.BaseView) {
            //  背景
            mBackgroundNormal = getColor(R.styleable.BaseView_background_normal, mBackgroundNormal)
            //  角度
            mCornerRadius = getDimensionPixelSize(R.styleable.BaseView_corner_radius, mCornerRadius)
            mCornerRadiusTopLeft = getDimensionPixelSize(R.styleable.BaseView_corner_radius_top_left, mCornerRadiusTopLeft)
            mCornerRadiusTopRight = getDimensionPixelSize(R.styleable.BaseView_corner_radius_top_right, mCornerRadiusTopRight)
            mCornerRadiusBottomLeft = getDimensionPixelSize(R.styleable.BaseView_corner_radius_bottom_left, mCornerRadiusBottomLeft)
            mCornerRadiusBottomRight = getDimensionPixelSize(R.styleable.BaseView_corner_radius_bottom_right, mCornerRadiusBottomRight)
            //  边框
            mBorderWidth = getDimensionPixelSize(R.styleable.BaseView_border_width, mBorderWidth)
            mBorderColor = getColor(R.styleable.BaseView_border_color, mBorderColor)
        }
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
                mCornerRadiusBottomRight.toFloat(), mCornerRadiusBottomRight.toFloat(),
                mCornerRadiusBottomLeft.toFloat(), mCornerRadiusBottomLeft.toFloat()
            )
        }
        //  边框
        shape.setStroke(mBorderWidth, mBorderColor)
        view.background = shape
    }

    override fun setBackgroundColorNormal(@ColorInt color: Int): BaseHelper<*> {
        if (mBackgroundNormal == color) {
            return this
        }
        mBackgroundNormal = color
        initDrawableSet()
        return this
    }

    override fun setCornerRadius(@Px radius: Int): BaseHelper<*> {
        if (mCornerRadius == radius) {
            return this
        }
        mCornerRadius = radius
        initDrawableSet()
        return this
    }

    override fun setCorners(
        @Px topLeft: Int,
        @Px topRight: Int,
        @Px bottomLeft: Int,
        @Px bottomRight: Int
    ): BaseHelper<*> {
        if (
            mCornerRadius < 0 &&
            mCornerRadiusTopLeft == topLeft &&
            mCornerRadiusTopRight == topRight &&
            mCornerRadiusBottomLeft == bottomLeft &&
            mCornerRadiusBottomRight == bottomRight
        ) {
            return this
        }
        mCornerRadius = -1
        mCornerRadiusTopLeft = topLeft
        mCornerRadiusTopRight = topRight
        mCornerRadiusBottomLeft = bottomLeft
        mCornerRadiusBottomRight = bottomRight
        initDrawableSet()
        return this
    }

    override fun setBorder(@Px width: Int, @ColorInt color: Int): BaseHelper<*> {
        if (mBorderWidth == width && mBorderColor == color) {
            return this
        }
        mBorderWidth = width
        mBorderColor = color
        initDrawableSet()
        return this
    }

    override fun setBorderWidth(@Px width: Int): BaseHelper<*> {
        if (mBorderWidth == width) {
            return this
        }
        mBorderWidth = width
        initDrawableSet()
        return this
    }

    override fun setBorderColor(@ColorInt color: Int): BaseHelper<*> {
        if (mBorderColor == color) {
            return this
        }
        mBorderColor = color
        initDrawableSet()
        return this
    }

}
