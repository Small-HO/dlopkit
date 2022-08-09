package com.small.uikit.ui.view

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.small.uikit.R

/**
 * Created by small-ho on 2022/07 18:37
 * title:
 */
class UiRecyclerView : RecyclerView {

    private var mLayoutEmpty = 0

    private val emptyObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            if (adapter != null && mLayoutEmpty != 0) {
                if (adapter?.itemCount == 0) {
                    Log.e("测试----------------","11111----------")
//                    val layout = LayoutInflater.from(context).inflate(mLayoutEmpty,null)
                }
            }
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UiRecyclerView)
        mLayoutEmpty = typedArray.getResourceId(R.styleable.UiRecyclerView_layout_empty, 0)
        typedArray.recycle()
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)
        emptyObserver.onChanged()
    }

}