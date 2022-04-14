package com.small.corekit.view

import android.content.Context
import android.view.View

/**
 * Created by small-ho on 2022/04 15:52
 * title: 悬浮框创建接口
 */
interface DlopView {

    fun onCreate(context: Context)

    fun onCreateView(context: Context): View

    fun onViewCreated(view: View)

}