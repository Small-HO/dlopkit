package com.small.libcommon.ktx

import android.view.View

/**
 * Created by small-ho on 2022/03 11:43
 * title: 控件点击帮助类
 */
var checkListenerTime = 0L
/** 单击防抖动 */
fun <T : View> T.singleClickListener(interval: Long = 500, block: (T) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (checkListenerTime != 0L && (currentTime - checkListenerTime < interval)) { return@setOnClickListener }
        checkListenerTime = currentTime
        block(this)
    }
}
/** 双击防抖动 */
fun <T : View> T.dblClickListener(single: (T) -> Unit,double: (T) -> Unit) {
    setOnClickListener {
        val secondTime = System.currentTimeMillis()
        if (secondTime - checkListenerTime > 200) {
            checkListenerTime = secondTime
            single(this)
        } else {
            double(this)
        }
    }
}