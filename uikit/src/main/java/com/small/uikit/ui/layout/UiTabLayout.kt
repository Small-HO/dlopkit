package com.small.uikit.ui.layout

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import com.small.uikit.helper.BaseHelper
import com.small.uikit.helper.UiHelper

/**
 * Created by small-ho on 2022/06 10:10
 * title: 重定义TabLayout样式
 */
class UiTabLayout : TabLayout , UiHelper<BaseHelper<*>> {

    private var mHelper: BaseHelper<*>? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mHelper = BaseHelper(context, this, attrs)
    }

    override var helper: BaseHelper<*>? = mHelper

}