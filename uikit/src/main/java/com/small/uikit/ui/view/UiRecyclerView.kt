package com.small.uikit.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.RecyclerView
import com.small.uikit.R

/**
 * Created by small-ho on 2022/07 18:37
 * title: 自定义带有空布局的RecyclerView
 */
class UiRecyclerView : RecyclerView {

    private var mLayoutEmpty = 0
    private var mEmptyView: View? = null
    private var isObserverRegistered = false

    private val emptyObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            checkEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkEmpty()
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, R.styleable.UiRecyclerView) {
            mLayoutEmpty = getResourceId(R.styleable.UiRecyclerView_layout_empty, 0)
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        unregisterObserver()
        super.setAdapter(adapter)
        registerObserver()
        checkEmpty()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerObserver()
        checkEmpty()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregisterObserver()
    }

    private fun registerObserver() {
        if (!isObserverRegistered && adapter != null) {
            adapter?.registerAdapterDataObserver(emptyObserver)
            isObserverRegistered = true
        }
    }

    private fun unregisterObserver() {
        if (isObserverRegistered && adapter != null) {
            adapter?.unregisterAdapterDataObserver(emptyObserver)
            isObserverRegistered = false
        }
    }

    /**
     * 检查并显示空布局
     */
    private fun checkEmpty() {
        val adapter = adapter
        val parent = parent as? ViewGroup
        if (adapter != null && mLayoutEmpty != 0 && parent != null) {
            val isEmpty = adapter.itemCount == 0
            if (isEmpty) {
                if (mEmptyView == null) {
                    mEmptyView = LayoutInflater.from(context).inflate(mLayoutEmpty, parent, false)
                    mEmptyView?.layoutParams = this.layoutParams
                    parent.addView(mEmptyView)
                }
                mEmptyView?.visibility = VISIBLE
                this.visibility = GONE
            } else {
                mEmptyView?.visibility = GONE
                this.visibility = VISIBLE
            }
        }
    }

}
