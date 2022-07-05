package com.small.uikit.ui.view

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.small.uikit.helper.BaseHelper
import com.small.uikit.helper.UiHelper
import java.lang.Float.max
import kotlin.math.ceil

/**
 * Created by small-ho on 2022/06 17:57
 * title: 重定义textView样式
 */
class UiTextView : AppCompatTextView , UiHelper<BaseHelper<*>> {

    private var mHelper: BaseHelper<*>? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        mHelper = BaseHelper(context, this, attrs)
    }

    override var helper: BaseHelper<*>? = mHelper

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var maxWidth = ceil(getMaxLineWidth(layout)).toInt()
        maxWidth += paddingRight + paddingLeft
        setMeasuredDimension(maxWidth, measuredHeight)
    }

    private fun getMaxLineWidth(layout: Layout): Float {
        var maximumWidth = 0.0f
        val lines = layout.lineCount
        for (i in 0 until lines) {
            maximumWidth = max(layout.getLineWidth(i), maximumWidth)
        }
        return maximumWidth
    }

}