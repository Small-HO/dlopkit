package com.small.floatkit.manager

import android.content.Context
import com.small.floatkit.view.PopDlopView

/**
 * Created by small-ho on 2022/03 11:14
 * title: 悬浮框管理类
 */
object PopDlopManager {


    fun init(context: Context) {
        PopDlopView.instance.initPopDlopView(context)
    }

    /** 显示 */
    fun showPopView() {

    }

    /** 隐藏 */
    fun dismissPopView() {

    }

}