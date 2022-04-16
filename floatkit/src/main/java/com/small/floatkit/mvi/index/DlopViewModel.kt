package com.small.floatkit.mvi.index

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.small.libcommon.ktx.SingleLiveEvents
import com.small.libcommon.ktx.asLiveData
import com.small.libcommon.ktx.setEvent
import kotlinx.coroutines.flow.*
import com.small.libcommon.utils.EmptyUtils
import me.jessyan.retrofiturlmanager.RetrofitUrlManager

/**
 * Created by small-ho on 2022/04 11:19
 * title: 测试模式 viewmodel
 */
class DlopViewModel: ViewModel() {

    private val _viewStates = MutableStateFlow(DlopViewState())
    val viewStates: StateFlow<DlopViewState> = _viewStates.asStateFlow()
    private val _viewEvent: SingleLiveEvents<DlopViewEvent> = SingleLiveEvents()
    val viewEvents: LiveData<List<DlopViewEvent>> = _viewEvent.asLiveData()

    /** 事件入口 */
    fun dispatch(viewAction: DlopViewAction) {
        when(viewAction) {
            is DlopViewAction.CheckDebug -> saveDebugState(viewAction.http_dev)
            is DlopViewAction.CheckRelease -> saveReleaseState(viewAction.http_main)
            is DlopViewAction.CheckLocal -> saveLocalState(viewAction.http_local)
        }
    }

    /** 保存测试环境 */
    private fun saveDebugState(http: String, state: Int = 0) {
        _viewStates.value = DlopViewState(ipConfigState = state)
        RetrofitUrlManager.getInstance().setGlobalDomain(http)
        _viewEvent.setEvent(DlopViewEvent.ShowToast("已切换测试环境"))
    }

    /** 保存正式环境 */
    private fun saveReleaseState(http: String, state: Int = 1) {
        _viewStates.value = DlopViewState(ipConfigState = state)
        RetrofitUrlManager.getInstance().setGlobalDomain(http)
        _viewEvent.setEvent(DlopViewEvent.ShowToast("已切换正式环境"))
    }

    /** 保存本地环境 */
    private fun saveLocalState(ip: String, state: Int = 2) {
        _viewStates.value = DlopViewState(ipConfigState = state)
        if (EmptyUtils.isEmpty(ip)) {
            _viewEvent.setEvent(DlopViewEvent.ShowToast("请输入自定义地址"))
        }else {
            RetrofitUrlManager.getInstance().setGlobalDomain(ip)
            _viewEvent.setEvent(DlopViewEvent.ShowToast("已切换自定义环境"))
        }
    }


}