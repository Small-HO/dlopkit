package com.small.uikit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.small.uikit.R
import kotlin.math.abs

/**
 * Created by small-ho on 2022/08 17:55
 * title: 自定义分屏控件
 */
class UiSplitView : ViewGroup , View.OnTouchListener {

    private var mViewId = 0         //  分屏view

    private var mViewRatio = 0.7F   //  分屏比例
    private var mViewTop = 0        //  分屏顶部
    private var mViewBottom = 0     //  分屏底部

    private var conHeight = 0
    private var mTouchSlop = 0
    private var mSplitHeight = 0
    private var mLastMotionY = 0

    private var mTop: View? = null
    private var mMid: View? = null
    private var mBottom: View? = null

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initAttributeSet(context, attrs)
    }

    private fun initAttributeSet(context: Context?, attrs: AttributeSet?) {
        if (context == null || attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UiSplitView)
        mViewId = typedArray.getResourceId(R.styleable.UiSplitView_view_id, mViewId)
        if (mViewId == 0) {
            throw IllegalArgumentException("必须添加分屏手柄控件")
        }
        mViewRatio = typedArray.getFloat(R.styleable.UiSplitView_view_ratio, mViewRatio)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount != 3) {
            throw RuntimeException("需要添加3个控件")
        }
        conHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (mSplitHeight == 0) {
            mSplitHeight = (conHeight * mViewRatio).toInt()
        }
        measureChild(mTop, widthMeasureSpec, MeasureSpec.makeMeasureSpec(mSplitHeight, MeasureSpec.EXACTLY))
        measureChild(mMid, widthMeasureSpec, heightMeasureSpec)
        measureChild(mBottom, widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredHeight - mSplitHeight, MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mTop?.let { it.layout(0, 0, 0 + it.measuredWidth, 0 + it.measuredHeight) }
        mMid?.let { it.layout(0, 0 + mSplitHeight - it.measuredHeight, 0 + it.measuredWidth, 0 + mSplitHeight) }
        mBottom?.let { it.layout(0, 0 + mSplitHeight, 0 + it.measuredWidth, 0 + mSplitHeight + it.measuredHeight) }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        mTop = getChildAt(0)
        mMid = getChildAt(1)
        mBottom = getChildAt(2)
        var handlerView = mMid?.findViewById<View>(mViewId)
        if (handlerView == null) {
            handlerView = mMid
        }
        handlerView?.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastMotionY = event.y.toInt()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val delta: Int = (event.y - mLastMotionY).toInt()
                if (abs(delta) > mTouchSlop) {
                    mSplitHeight += if (delta > 0) {
                        delta - mTouchSlop
                    } else {
                        delta + mTouchSlop
                    }
                    if (mSplitHeight < mViewTop || mSplitHeight < (mMid?.height ?: 0)) {
                        mSplitHeight = mMid?.height?.let { mViewTop.coerceAtLeast(it) } ?: 0
                    } else if (mSplitHeight > height || mSplitHeight > height - mViewBottom) {
                        mSplitHeight = height.coerceAtMost(height - mViewBottom)
                    }
                    requestLayout()
                }
                return true
            }
        }
        return false
    }

    fun getMidHeight(): Int = mMid?.height ?: 0
    fun getSplitHeight(ratio: Float): Int = (conHeight * ratio).toInt()

    fun setSplitRatio(sSplitRatio: Float) {
        mSplitHeight = (conHeight * sSplitRatio).toInt()
        requestLayout()
    }

}