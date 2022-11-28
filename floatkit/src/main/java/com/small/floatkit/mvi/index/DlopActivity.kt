package com.small.floatkit.mvi.index

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.small.floatkit.R
import com.small.floatkit.common.DlopConfig
import com.small.floatkit.databinding.DlopActivityLayoutBinding
import com.small.libcommon.base.BaseVMActivity
import com.small.libcommon.ktx.observeEvent
import kotlinx.coroutines.launch
import me.jessyan.retrofiturlmanager.RetrofitUrlManager

class DlopActivity : BaseVMActivity<DlopActivityLayoutBinding>() {

    private val config by lazy { intent.extras?.getSerializable("config") as DlopConfig? }

    private val viewModel by lazy { ViewModelProvider(this)[DlopViewModel::class.java] }

    override fun initLayout(): Int = R.layout.dlop_activity_layout

    override fun initView(state: Bundle?) {
        RetrofitUrlManager.getInstance().globalDomain?.let {
            binding.tvHttp.text = getString(R.string.dlop_http,it)
        }
    }

    override fun initClick() {
        config?.let { http ->
            /** 环境切换 */
            binding.debug.setOnClickListener { viewModel.dispatch(DlopViewAction.CheckDebug(http.http_dev)) }
            binding.release.setOnClickListener { viewModel.dispatch(DlopViewAction.CheckRelease(http.http_main)) }
            binding.local.setOnClickListener { viewModel.dispatch(DlopViewAction.CheckLocal(DlopViewState().ipConfig)) }
            binding.tvSave.setOnClickListener { viewModel.dispatch(DlopViewAction.CheckLocal(binding.etHost.text.toString())) }
        }?: kotlin.run {
            Toast.makeText(this,"未配置环境,无法切换",Toast.LENGTH_SHORT).show()
        }
        
        /** 网络管理 */
        binding.tvNetwork.setOnClickListener {
            Toast.makeText(this, "待开发",Toast.LENGTH_SHORT).show()
        }

        /** 方法耗时 */
        binding.tvTime.setOnClickListener {

        }
    }

    override fun initViewStates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewStates.collect { initStateView(it) }
            }
        }
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is DlopViewEvent.ShowToast -> Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** view初始化 */
    private fun initStateView(state: DlopViewState) {
        binding.etHost.setText(state.ipConfig)
        binding.llConfig.visibility = if (state.isShowLocalView) View.VISIBLE else View.GONE
        when (state.ipConfigState) {
            0 -> binding.debug.isChecked = true
            1 -> binding.release.isChecked = true
            2 -> binding.local.isChecked = true
        }
    }

}