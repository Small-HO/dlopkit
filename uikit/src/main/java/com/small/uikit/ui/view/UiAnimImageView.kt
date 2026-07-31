package com.small.uikit.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import com.small.uikit.R
import kotlin.math.roundToInt

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

    /** 图片宽高比缓存 */
    private var imageRatio = 0f
    /** Shape缓存 */
    private var lastShapeHash = Int.MIN_VALUE
    /** ColorStateList缓存 */
    private var borderColorStateList = ColorStateList.valueOf(Color.TRANSPARENT)

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
        borderColorStateList = ColorStateList.valueOf(borderColor)
        applyShape()
    }

    /** Shape更新 */
    private fun applyShape() {
        val shapeHash = arrayOf(
            isCircle,
            cornerRadius,
            topLeftRadius,
            topRightRadius,
            bottomLeftRadius,
            bottomRightRadius,
            borderWidth,
            borderColor
        ).contentHashCode()
        if (shapeHash == lastShapeHash) {
            return
        }
        lastShapeHash = shapeHash
        val builder = ShapeAppearanceModel.builder()
        when {
            isCircle -> builder.setAllCornerSizes(RelativeCornerSize(0.5f))
            cornerRadius > 0f -> builder.setAllCornerSizes(cornerRadius)
            else -> builder.setTopLeftCornerSize(topLeftRadius)
                .setTopRightCornerSize(topRightRadius)
                .setBottomLeftCornerSize(bottomLeftRadius)
                .setBottomRightCornerSize(bottomRightRadius)
        }
        shapeAppearanceModel = builder.build()
        strokeWidth = borderWidth
        strokeColor = borderColorStateList
    }

    /** 缓存图片比例 */
    private fun updateImageRatio(drawable: Drawable?) {
        imageRatio = if (drawable != null && drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
            drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth
        } else 0f
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        updateImageRatio(drawable)
        if (autoMeasure) {
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!autoMeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (imageRatio <= 0f) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (width * imageRatio).roundToInt()
        setMeasuredDimension(width, height)
    }

    fun setCircle(enable: Boolean) {
        if (isCircle == enable) {
            return
        }
        isCircle = enable
        applyShape()
    }

    fun setCornerRadius(radius: Float) {
        if (cornerRadius == radius) {
            return
        }
        cornerRadius = radius
        applyShape()
    }

    fun setBorder(width: Float, color: Int) {
        var changed = false
        if (borderWidth != width) {
            borderWidth = width
            changed = true
        }
        if (borderColor != color) {
            borderColor = color
            borderColorStateList = ColorStateList.valueOf(color)
            changed = true
        }
        if (changed) {
            applyShape()
        }
    }

    fun setCorners(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        if (
            this.topLeftRadius == topLeft &&
            this.topRightRadius == topRight &&
            this.bottomLeftRadius == bottomLeft &&
            this.bottomRightRadius == bottomRight
        ) {
            return
        }
        this.topLeftRadius = topLeft
        this.topRightRadius = topRight
        this.bottomLeftRadius = bottomLeft
        this.bottomRightRadius = bottomRight
        applyShape()
    }
}