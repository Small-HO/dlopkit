package com.small.floatkit.mvi.index

import com.small.libcommon.utils.SystemUtils

/**
 * Created by small-ho on 2022/04 11:26
 * title: 测试模式状态管理
 */

/** 地址状态管理 0:测试地址，1:正式地址，2:自定义地址*/
data class DlopViewState(var ipConfigState: Int? = null) {
    //  是否显示自定义View
    val isShowLocalView: Boolean = when(ipConfigState) {
        2 -> true
        else -> false
    }
    //  地址设置
    val ipConfig: String = "http://" + SystemUtils.getGateWay() + ":8080"
}

sealed class DlopViewEvent {
    data class ShowToast(val message: String): DlopViewEvent()
}

sealed class DlopViewAction {
    class CheckDebug(val http_dev: String): DlopViewAction()
    class CheckRelease(val http_main: String): DlopViewAction()
    class CheckLocal(val http_local: String) : DlopViewAction()
}