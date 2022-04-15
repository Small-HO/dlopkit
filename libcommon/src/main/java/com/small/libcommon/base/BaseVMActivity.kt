package com.small.libcommon.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding

/**
 * Created by small-ho on 2022/04 17:10
 * title: 基础类处理
 */
abstract class BaseVMActivity<DB : ViewDataBinding>: AbsVMActivity<DB>() {
    override fun initView(state: Bundle?) {}
    override fun initClick() {}
    override fun initViewStates() {}
    override fun initViewEvents() {}
}