package com.small.libcommon.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.core.widget.NestedScrollView

/**
 * Created by small-ho on 2020/12 8:22 PM
 * title: 滑动优化
 */
class XScrollView: NestedScrollView {

    private var inner: View? = null
    private var y = 0
    private var size = 4
    private val normal = Rect()

    private var mScrollListener: ((action: Int, dy: Float) -> Unit)? = null
    fun setOnScrollListener(listener: (action: Int, dy: Float) -> Unit) { this.mScrollListener = listener }

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            inner = getChildAt(0)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        mScrollListener?.let { it(-1, scrollY * 0.65f) }    //  使位移效果更加平滑 解决手指按住停留时抖动问题
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (inner == null) {
            return super.onTouchEvent(ev)
        }else {
            commOnTouchEvent(ev)
        }
        return super.onTouchEvent(ev)
    }

    private fun commOnTouchEvent(ev: MotionEvent) {
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> y = ev.y.toInt()
            MotionEvent.ACTION_UP -> {
                // 是否须要开启动画
                if (!normal.isEmpty) {
                    inner?.let {
                        // 开启移动动画
                        val ta = TranslateAnimation(0F, 0F, it.top.toFloat(), normal.top.toFloat())
                        ta.duration = 200
                        it.startAnimation(ta)
                        // 设置回到正常的布局位置
                        it.layout(normal.left, normal.top, normal.right, normal.bottom)
                    }
                    normal.setEmpty()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val preY = y
                val nowY = ev.y
                val deltaY = (preY - nowY).toInt() / size
                y = nowY.toInt()
                // 当滚动到最上或者最下时就不会再滚动。这时移动布局
                inner?.let {
                    if (scrollY == 0 || scrollY == it.measuredHeight - height) {
                        if (normal.isEmpty) {
                            // 保存正常的布局位置
                            normal[it.left, it.top, it.right] = it.bottom
                            return
                        }
                        val yy = it.top - deltaY
                        // 移动布局
                        it.layout(it.left, yy, it.right, it.bottom - deltaY)
                    }
                }
            }
        }
    }

}