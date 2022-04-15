package com.small.floatkit.view

import android.content.Context
import android.content.Intent
import android.view.*
import com.small.floatkit.R
import com.small.floatkit.mvi.index.DlopActivity

/**
 * Created by small-ho on 2022/03 10:27
 * title: 浮框
 */
class PopDlopView : AbsDlopView() {

    companion object {
        val instance: PopDlopView by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PopDlopView()
        }
    }

    /** 初始化 */
    fun initPopDlopView(context: Context) {
        foundDlopView(context)
    }

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.dlop_control_view_layout, null)
    }

    override fun onViewCreated(view: View) {
        view.setOnClickListener {
            view.context.startActivity(Intent(view.context, DlopActivity::class.java))
        }
    }

}
