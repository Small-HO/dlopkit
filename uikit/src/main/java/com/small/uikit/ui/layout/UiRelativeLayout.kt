package com.small.uikit.ui.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.small.uikit.helper.BaseHelper
import com.small.uikit.helper.UiHelper

/**
 * Created by small-ho on 2022/06 10:08
 * title: 重定义RelativeLayout样式
 */
class UiRelativeLayout : RelativeLayout , UiHelper<BaseHelper<*>> {

    override var helper: BaseHelper<*>? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        helper = BaseHelper(context, this, attrs)
    }

}