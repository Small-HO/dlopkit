package com.small.uikit.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.small.uikit.R
import com.small.uikit.helper.DrawableHelper

/**
 * Created by small-ho on 2022/06 10:19
 * title: 重定义ImageView样式
 */
class ImageView : AppCompatImageView {

    private var mIsCircle = false
    private var mCornerRadius = -1
    private var mCornerRadiusTopLeft = 0
    private var mCornerRadiusTopRight = 0
    private var mCornerRadiusBottomLeft = 0
    private var mCornerRadiusBottomRight = 0

    private var mBorderWidth = 0
    private var mmBorderColor = Color.BLACK

    private var mResource = 0
    private var mDrawable: Drawable? = null
    private var mScaleType: ScaleType? = null

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        initAttributeSet(context, attrs)
        initDrawableSet()
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(scaleType)
        if (mScaleType != scaleType) {
            mScaleType = scaleType
            initDrawableSet()
            invalidate()
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        mResource = 0
        mDrawable = DrawableHelper.fromBitmap(bm)
        initDrawableSet()
        super.setImageDrawable(mDrawable)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        mResource = 0
        mDrawable = DrawableHelper.fromDrawable(drawable)
        initDrawableSet()
        super.setImageDrawable(mDrawable)
    }

    override fun setBackground(background: Drawable?) {
        mResource = 0
        mDrawable = DrawableHelper.fromDrawable(background)
        initDrawableSet()
        super.setBackground(mDrawable)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        if (mResource != resId) {
            mResource = resId
            mDrawable = resolveResource()
            initDrawableSet()
            super.setImageDrawable(mDrawable)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawEmptyBitmap()
    }

    private fun drawEmptyBitmap() {
        if (mDrawable == null) {
            val width = measuredWidth
            val height = measuredHeight
            if (width > 0 && height > 0) {
                val background = background
                if (background != null) {
                    background.setBounds(0, 0, width, height)
                    setImageDrawable(background)
                } else {
                    setImageBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8))
                }
            }
        }
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageView)
            mIsCircle = typedArray.getBoolean(R.styleable.ImageView_is_circle, mIsCircle)
            mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.ImageView_corner_radius, mCornerRadius)
            mCornerRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.ImageView_corner_radius_top_left, mCornerRadiusTopLeft)
            mCornerRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.ImageView_corner_radius_top_right, mCornerRadiusTopRight)
            mCornerRadiusBottomLeft = typedArray.getDimensionPixelSize(R.styleable.ImageView_corner_radius_bottom_left, mCornerRadiusBottomLeft)
            mCornerRadiusBottomRight = typedArray.getDimensionPixelSize(R.styleable.ImageView_corner_radius_bottom_right, mCornerRadiusBottomRight)
            mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.ImageView_border_width, mBorderWidth)
            mmBorderColor = typedArray.getColor(R.styleable.ImageView_border_color, mmBorderColor)
            typedArray.recycle()
        }
    }

    private fun initDrawableSet() {
        updateDrawableAttrs(mDrawable, mScaleType)
    }

    private fun updateDrawableAttrs(drawable: Drawable?, scaleType: ScaleType?) {
        if (drawable == null) return
        when(drawable) {
            is DrawableHelper -> drawable.setParams(scaleType, mBorderWidth.toFloat(), mmBorderColor, mIsCircle, mCornerRadius.toFloat(), mCornerRadiusTopLeft.toFloat(), mCornerRadiusTopRight.toFloat(), mCornerRadiusBottomLeft.toFloat(), mCornerRadiusBottomRight.toFloat())
            is LayerDrawable -> {
                var i = 0
                val layers = drawable.numberOfLayers
                while (i < layers) {
                    updateDrawableAttrs(drawable.getDrawable(i), scaleType)
                    i++
                }
            }
        }
    }

    private fun resolveResource(): Drawable? {
        val context = context ?: return null
        var d: Drawable? = null
        if (mResource != 0) {
            try {
                d = ContextCompat.getDrawable(context,mResource)
            } catch (e: Exception) {
                mResource = 0
            }
        }
        return DrawableHelper.fromDrawable(d)
    }

}