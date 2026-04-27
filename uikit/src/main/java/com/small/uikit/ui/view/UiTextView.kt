package com.small.uikit.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.small.uikit.helper.BaseHelper
import com.small.uikit.helper.UiHelper

/**
 * Created by small-ho on 2022/06 17:57
 * title: 重定义textView样式
 */
class UiTextView : AppCompatTextView , UiHelper<BaseHelper<*>> {

    override var helper: BaseHelper<*>? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        helper = BaseHelper(context, this, attrs)
    }

}