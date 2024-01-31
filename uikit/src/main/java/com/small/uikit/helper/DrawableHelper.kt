package com.small.uikit.helper

import android.graphics.*
import android.widget.ImageView.ScaleType
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.Matrix.ScaleToFit
import android.graphics.drawable.Drawable
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Created by small-ho on 2022/06 11:22
 * title: Drawable帮助类
 */
class DrawableHelper(val bitmap: Bitmap) : Drawable() {
    private val mBitmapWidth: Int = bitmap.width
    private val mBitmapHeight: Int = bitmap.height
    private val mBitmapRect = RectF()
    private val mBitmapPaint: Paint
    private val mBorderPaint: Paint
    private val mBorderRect = RectF()
    private val mDrawableRect = RectF()
    private val mPath = Path()
    private var mRectF = RectF()
    private val mBounds = RectF()
    private val mBoundsFinal = RectF()
    private var mRebuildShader = true
    private val mShaderMatrix = Matrix()
    private var mScaleType = ScaleType.FIT_CENTER

    private var mCircle = true

    private var mCorner = -1f
    private var mCornerTopLeft = 0f
    private var mCornerTopRight = 0f
    private var mCornerBottomLeft = 0f
    private var mCornerBottomRight = 0f
    private val mCornerRadii = FloatArray(8)

    private var mBorderWidth = 0f
    private var mBorderColor = Color.BLACK

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mBoundsFinal.set(bounds) //更新Bounds
        updateShaderMatrix() //更新变化矩阵
        updateConner() //更新圆角
    }

    override fun setAlpha(alpha: Int) {
        mBitmapPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mBitmapPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun draw(canvas: Canvas) {
        if (mRebuildShader) {
            val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            bitmapShader.setLocalMatrix(mShaderMatrix) // Shader.TileMode.CLAMP
            mBitmapPaint.shader = bitmapShader
            mRebuildShader = false
        }
        if (mCircle) {
            // mDrawableRect 宽高是实际显示图片的宽高，类似于 marginTop 了  mDrawableRect.left
            var cx = mDrawableRect.width() / 2f + mDrawableRect.left
            var cy = mDrawableRect.height() / 2f + mDrawableRect.top
            var radiusX = mDrawableRect.width() / 2f
            var radiusY = mDrawableRect.height() / 2f
            var radiusDrawable = min(radiusX, radiusY)
            var radiusBitmap = min(mBitmapHeight, mBitmapWidth).toFloat()
            var radius = min(radiusBitmap, radiusDrawable)
            canvas.drawCircle(cx, cy, radius, mBitmapPaint)
            if (mBorderWidth > 0) {
                cx = mBorderRect.width() / 2f + mBorderRect.left
                cy = mBorderRect.height() / 2f + mBorderRect.top
                radiusX = mBorderRect.width() / 2f
                radiusY = mBorderRect.height() / 2f
                radiusDrawable = min(radiusX, radiusY)
                radiusBitmap = min(mBitmapHeight, mBitmapWidth).toFloat()
                radius = min(radiusBitmap, radiusDrawable)
                canvas.drawCircle(cx, cy, radius, mBorderPaint)
            }
        } else {
            updateDrawablePath()
            canvas.drawPath(mPath, mBitmapPaint)
            if (mBorderWidth > 0) {
                updateBorderPath()
                canvas.drawPath(mPath, mBorderPaint)
            }
        }
    }

    /**
     * 更新圆角
     */
    private fun updateConner() {
        if (mCorner >= 0) {
            Arrays.fill(mCornerRadii, mCorner)
            return
        }
        if (mCorner < 0) {
            mCornerRadii[0] = mCornerTopLeft
            mCornerRadii[1] = mCornerTopLeft
            mCornerRadii[2] = mCornerTopRight
            mCornerRadii[3] = mCornerTopRight
            mCornerRadii[4] = mCornerBottomRight
            mCornerRadii[5] = mCornerBottomRight
            mCornerRadii[6] = mCornerBottomLeft
            mCornerRadii[7] = mCornerBottomLeft
        }
    }

    /**
     * 更新Drawable路径
     */
    private fun updateDrawablePath() {
        mPath.reset() //must重置
        mPath.addRoundRect(mDrawableRect, mCornerRadii, Path.Direction.CCW)
    }

    /**
     * 更新边框路径
     */
    private fun updateBorderPath() {
        mPath.reset() //must重置
        mPath.addRoundRect(mBorderRect, mCornerRadii, Path.Direction.CCW)
    }

    /**
     * 根据ScaleType更新ShaderMatrix
     * 此函数涉及更新的属性：mBorderWidth || mScaleType || mCircle
     */
    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        val height: Float
        val width: Float
        val half = mBorderWidth / 2f
        mBounds.set(mBoundsFinal)
        when (mScaleType) {
            ScaleType.CENTER_INSIDE -> {
                if (mBitmapWidth <= mBounds.width() && mBitmapHeight <= mBounds.height()) {
                    scale = 1.0f
                    height = mBitmapHeight.toFloat()
                    width = mBitmapWidth.toFloat()
                } else { //bitmap > drawable
                    scale = min(mBounds.width() / mBitmapWidth.toFloat(), mBounds.height() / mBitmapHeight.toFloat())
                    if (mBounds.height() < mBounds.width()) { //高<宽
                        height = mBounds.height()
                        width = mBitmapWidth * scale
                    } else if (mBounds.height() > mBounds.width()) { //宽<高
                        height = mBitmapHeight * scale
                        width = mBounds.width()
                    } else { //宽=高
                        height = mBitmapHeight * scale
                        width = mBitmapWidth * scale
                    }
                }
                //X,Y偏移
                dx = ((mBounds.width() - mBitmapWidth * scale) * 0.5f + 0.5f).toInt().toFloat()
                dy = ((mBounds.height() - mBitmapHeight * scale) * 0.5f + 0.5f).toInt().toFloat()
                mRectF = RectF(dx, dy, width + dx, height + dy)
                mRectF.inset(if (mCircle) mBorderWidth else half, if (mCircle) mBorderWidth else half) //非圆 1/2兼容圆角
                mShaderMatrix.reset()
                mShaderMatrix.setScale(scale, scale)
                mShaderMatrix.postTranslate(dx, dy)
            }
            ScaleType.CENTER -> {
                height = min(mBounds.height(), mBitmapRect.height())
                width = min(mBounds.width(), mBitmapRect.width())
                // 裁剪或者Margin（如果View大，则 margin Bitmap，如果View小则裁剪Bitmap）
                val cutOrMarginH = mBounds.height() - mBitmapRect.height()
                val cutOrMarginW = mBounds.width() - mBitmapRect.width()
                val halfH = cutOrMarginH / 2f
                val halfW = cutOrMarginW / 2f
                val top: Float = if (halfH > 0) halfH else 0F
                val left: Float = if (halfW > 0) halfW else 0F
                dx = halfW
                dy = halfH
                mRectF = RectF(left, top, left + width, top + height)
                mRectF.inset(if (mCircle) mBorderWidth else half, if (mCircle) mBorderWidth else half) //非圆 1/2兼容圆角
                mShaderMatrix.reset()
                mShaderMatrix.postTranslate((dx + 0.5f).toInt() + half, (dy + 0.5f).toInt() + half)
            }
            ScaleType.CENTER_CROP -> {
                mRectF.set(mBounds)
                mRectF.inset(if (mCircle) mBorderWidth else half, if (mCircle) mBorderWidth else half) //非圆 1/2兼容圆角
                if (mBitmapWidth * mRectF.height() > mRectF.width() * mBitmapHeight) {
                    scale = mRectF.height() / mBitmapHeight.toFloat()
                    dx = (mRectF.width() - mBitmapWidth * scale) * 0.5f
                } else {
                    scale = mRectF.width() / mBitmapWidth.toFloat()
                    dy = (mRectF.height() - mBitmapHeight * scale) * 0.5f
                }
                mShaderMatrix.reset()
                mShaderMatrix.setScale(scale, scale)
                mShaderMatrix.postTranslate((dx + 0.5f).toInt() + half, (dy + 0.5f).toInt() + half)
            }
            ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.FIT_START -> {
                mBounds.inset(if (mCircle) mBorderWidth else half, if (mCircle) mBorderWidth else half) //非圆 1/2兼容圆角
                mRectF.set(mBitmapRect)
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, scaleTypeToScaleToFit(mScaleType))
                mShaderMatrix.mapRect(mRectF)
                mShaderMatrix.setRectToRect(mBitmapRect, mRectF, ScaleToFit.FILL)
            }
            ScaleType.FIT_XY -> {
                mBounds.inset(if (mCircle) mBorderWidth else half, if (mCircle) mBorderWidth else half) //非圆 1/2兼容圆角
                mRectF.set(mBounds)
                mShaderMatrix.reset()
                mShaderMatrix.setRectToRect(mBitmapRect, mRectF, ScaleToFit.FILL)
            }
            else -> {
                mBounds.inset(if (mCircle) mBorderWidth else half, if (mCircle) mBorderWidth else half)
                mRectF.set(mBitmapRect)
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, scaleTypeToScaleToFit(mScaleType))
                mShaderMatrix.mapRect(mRectF)
                mShaderMatrix.setRectToRect(mBitmapRect, mRectF, ScaleToFit.FILL)
            }
        }
        if (mCircle) {
            mBorderRect[mRectF.left - half, mRectF.top - half, mRectF.right + half] = mRectF.bottom + half //还原
        } else {
            mBorderRect.set(mBoundsFinal)
            mBorderRect.inset(half, half)
        }
        mDrawableRect.set(mRectF)
        mRebuildShader = true
    }

    /**
     * 更新边框
     */
    private fun updateBorder() {
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth
    }

    /**
     * 统一设置参数
     */
    fun setParams(scaleType: ScaleType?, borderWidth: Float, borderColor: Int, circle: Boolean, corner: Float, topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        // scaleType
        var scaleType = scaleType
        if (scaleType == null) {
            scaleType = ScaleType.FIT_CENTER
        }
        if (mScaleType != scaleType) {
            mScaleType = scaleType
        }

        //borderWidth
        mBorderWidth = borderWidth
        //borderColor
        mBorderColor = borderColor
        updateBorder()

        //circle
        mCircle = circle

        //corner
        mCorner = corner
        mCornerTopLeft = topLeft
        mCornerTopRight = topRight
        mCornerBottomLeft = bottomLeft
        mCornerBottomRight = bottomRight
        updateConner()

        //update
        updateShaderMatrix() //更新变化矩阵
        invalidateSelf() //更新
    }

    fun setScaleType(scaleType: ScaleType?): DrawableHelper {
        var scaleType = scaleType
        if (scaleType == null) {
            scaleType = ScaleType.FIT_CENTER
        }
        if (mScaleType != scaleType) {
            mScaleType = scaleType
            updateShaderMatrix() //更新变化矩阵
            invalidateSelf()
        }
        return this
    }

    fun setCircle(circle: Boolean): DrawableHelper {
        mCircle = circle
        updateShaderMatrix() //更新变化矩阵
        invalidateSelf()
        return this
    }

    fun setBorderWidth(borderWidth: Float): DrawableHelper {
        mBorderWidth = borderWidth
        updateBorder()
        updateShaderMatrix() //更新变化矩阵
        invalidateSelf()
        return this
    }

    fun setBorderColor(borderColor: Int): DrawableHelper {
        mBorderColor = borderColor
        updateBorder()
        invalidateSelf()
        return this
    }

    fun setConner(corner: Float, topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float, ): DrawableHelper {
        mCorner = corner
        mCornerTopLeft = topLeft
        mCornerTopRight = topRight
        mCornerBottomLeft = bottomLeft
        mCornerBottomRight = bottomRight
        updateConner()
        invalidateSelf()
        return this
    }

    companion object {

        fun fromBitmap(bitmap: Bitmap?): DrawableHelper? {
            return bitmap?.let { DrawableHelper(it) }
        }

        fun fromDrawable(drawable: Drawable?): Drawable? {
            if (drawable != null) {
                when(drawable) {
                    is DrawableHelper -> return drawable
                    is LayerDrawable -> {
                        val cs = drawable.mutate().constantState
                        val ld = (cs?.newDrawable() ?: drawable) as LayerDrawable
                        val num = ld.numberOfLayers

                        // loop through layers to and change to RoundedDrawables if possible
                        for (i in 0 until num) {
                            val d = ld.getDrawable(i)
                            ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d))
                        }
                        return ld
                    }
                }

                // try to get a bitmap from the drawable and
                val bm = drawableToBitmap(drawable)
                if (bm != null) {
                    return DrawableHelper(bm)
                }
            }
            return drawable
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap? {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            var bitmap: Bitmap?
            val width = max(drawable.intrinsicWidth, 2)
            val height = max(drawable.intrinsicHeight, 2)
            try {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            } catch (e: Throwable) {
                e.printStackTrace()
                bitmap = null
            }
            return bitmap
        }

        private fun scaleTypeToScaleToFit(st: ScaleType): ScaleToFit {
            /**
             * 根据源码改造  sS2FArray[st.nativeInt - 1]
             */
            return when (st) {
                ScaleType.FIT_XY -> ScaleToFit.FILL
                ScaleType.FIT_START -> ScaleToFit.START
                ScaleType.FIT_END -> ScaleToFit.END
                ScaleType.FIT_CENTER -> ScaleToFit.CENTER
                else -> ScaleToFit.CENTER
            }
        }
    }

    init {
        mBitmapRect[0f, 0f, mBitmapWidth.toFloat()] = mBitmapHeight.toFloat()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBitmapPaint.style = Paint.Style.FILL
        mBitmapPaint.isAntiAlias = true
        mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        updateBorder()
    }
}