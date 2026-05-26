package com.small.uikit.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.small.uikit.R
import kotlin.math.ceil

class UiAnimImageView : ShapeableImageView {

    private var isCircle = false
    private var cornerRadius = 0f
    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f
    private var borderWidth = 0f
    private var borderColor = Color.TRANSPARENT
    private var autoMeasure = true

    constructor(context: Context) : super(context) {
        init(null)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.UiAnimImageView) {
            isCircle = getBoolean(R.styleable.UiAnimImageView_is_circle, false)
            cornerRadius = getDimension(R.styleable.UiAnimImageView_corner_radius, 0f)
            topLeftRadius = getDimension(R.styleable.UiAnimImageView_corner_radius_top_left, 0f)
            topRightRadius = getDimension(R.styleable.UiAnimImageView_corner_radius_top_right, 0f)
            bottomLeftRadius = getDimension(R.styleable.UiAnimImageView_corner_radius_bottom_left, 0f)
            bottomRightRadius = getDimension(R.styleable.UiAnimImageView_corner_radius_bottom_right, 0f)
            borderWidth = getDimension(R.styleable.UiAnimImageView_border_width, 0f)
            borderColor = getColor(R.styleable.UiAnimImageView_border_color, Color.TRANSPARENT)
            autoMeasure = getBoolean(R.styleable.UiAnimImageView_auto_measure, true)
        }
        updateShape()
    }

    private fun updateShape() {
        if (isCircle) {
            post {
                val radius = minOf(width, height) / 2f
                shapeAppearanceModel = ShapeAppearanceModel.builder()
                        .setAllCornerSizes(radius)
                        .build()
            }
        } else {
            val model = ShapeAppearanceModel.builder()
            if (cornerRadius > 0f) {
                model.setAllCornerSizes(cornerRadius)
            } else {
                model.setTopLeftCornerSize(topLeftRadius)
                    .setTopRightCornerSize(topRightRadius)
                    .setBottomLeftCornerSize(bottomLeftRadius)
                    .setBottomRightCornerSize(bottomRightRadius)
            }
            shapeAppearanceModel = model.build()
        }
        strokeWidth = borderWidth
        strokeColor = android.content.res.ColorStateList.valueOf(borderColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!autoMeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val d = drawable
        if (d == null || d.intrinsicWidth <= 0 || d.intrinsicHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = ceil(width.toDouble() * d.intrinsicHeight / d.intrinsicWidth).toInt()
        setMeasuredDimension(width, height)
    }

    fun setCircle(enable: Boolean) {
        isCircle = enable
        updateShape()
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        updateShape()
    }

    fun setBorder(width: Float, color: Int) {
        borderWidth = width
        borderColor = color
        updateShape()
    }

    fun setCorners(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        topLeftRadius = topLeft
        topRightRadius = topRight
        bottomLeftRadius = bottomLeft
        bottomRightRadius = bottomRight
        updateShape()
    }
}