package com.small.libcommon.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Created by small-ho on 2022/03 10:51 上午
 * title: Activity 基础类
 */
abstract class AbsVMActivity<DB : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var binding: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, initLayout())
        initView(savedInstanceState)
        initClick()
        initViewStates()
        initViewEvents()
    }

    abstract fun initLayout(): Int
    abstract fun initView(state: Bundle?)
    abstract fun initClick()
    abstract fun initViewStates()
    abstract fun initViewEvents()

}