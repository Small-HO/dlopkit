package com.small.floatkit.manager

import android.content.Context
import com.small.floatkit.common.DlopConfig
import com.small.floatkit.view.PopDlopView
import com.small.libcommon.utils.EmptyUtils
import me.jessyan.retrofiturlmanager.RetrofitUrlManager

/**
 * Created by small-ho on 2022/03 11:14
 * title: 悬浮框管理类
 */
object PopDlopManager {

    fun init(context: Context) {
        PopDlopView.instance.initPopDlopView(context)
    }
    fun init(context: Context, config: DlopConfig) {
        PopDlopView.instance.initPopDlopView(context, config)
    }

    /** 获取更改后的IP */
    fun getHttpUrl(): String {
        return if (EmptyUtils.isEmpty(RetrofitUrlManager.getInstance().globalDomain)) {
            ""
        }else {
            RetrofitUrlManager.getInstance().globalDomain.toString()
        }
    }

}