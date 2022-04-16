package com.small.floatkit.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.*


/**
 * Created by small-ho on 2022/04 15:56
 * title: 页面浮标抽象类 一般的悬浮窗都需要继承该抽象接口
 */
abstract class AbsDlopView: DlopView {

    /** 创建悬浮框 */
    fun foundDlopView(context: Context) {
        try {
            val layout = onCreateView(context)
            val windowManager = windowManager(context)
            val layoutParams = laoutParams()
            windowManager.addView(layout, layoutParams)
            onViewCreated(layout)
            layout.setOnTouchListener(setOnTouchListener(context, layout, windowManager, layoutParams))
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 窗口管理者 */
    private fun windowManager(context: Context): WindowManager {
        return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /** 布局参数 */
    private fun laoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().also {
            it.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            it.format = PixelFormat.TRANSPARENT
            it.gravity = Gravity.TOP or Gravity.START
            it.width = WindowManager.LayoutParams.WRAP_CONTENT
            it.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    /** 移动 */
    private var mPrevX = 0f
    private var mPrevY = 0f
    private var startTime = 0L
    private var endTime = 0L
    private var isCheck = false
    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener(context: Context, layout: View, windowManager: WindowManager, laoutParams: WindowManager.LayoutParams) = View.OnTouchListener { _, event ->
        val mWidth = context.resources.displayMetrics.widthPixels
        val mHeight = context.resources.displayMetrics.heightPixels
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = event.rawX
                mPrevY = event.rawY
                isCheck = false
                startTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                isCheck = true
                val deltaX: Float = event.rawX - mPrevX
                val deltaY: Float = event.rawY - mPrevY
                laoutParams.x += deltaX.toInt()
                laoutParams.y += deltaY.toInt()
                mPrevX = event.rawX
                mPrevY = event.rawY
                if (laoutParams.x < 0) laoutParams.x = 0
                if (laoutParams.x > mWidth - layout.width) laoutParams.x = mWidth - layout.width
                if (laoutParams.y < 0) laoutParams.y = 0
                if (laoutParams.y > mHeight - layout.height * 2) laoutParams.y = mHeight - layout.height * 2
                try {
                    windowManager.updateViewLayout(layout, laoutParams)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            MotionEvent.ACTION_CANCEL -> {}
            MotionEvent.ACTION_UP -> {
                endTime = System.currentTimeMillis()
                isCheck = (endTime - startTime) > 0.1 * 1000L
            }
        }
        isCheck
    }

}