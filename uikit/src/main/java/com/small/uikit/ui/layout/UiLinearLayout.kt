package com.small.uikit.ui.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.small.uikit.helper.BaseHelper
import com.small.uikit.helper.UiHelper

/**
 * Created by small-ho on 2022/06 10:03
 * title: 重定义LinearLayout样式
 */
class UiLinearLayout : LinearLayout , UiHelper<BaseHelper<*>> {

    private var mHelper: BaseHelper<*>? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mHelper = BaseHelper(context, this, attrs)
    }

    override var helper: BaseHelper<*>? = mHelper

}