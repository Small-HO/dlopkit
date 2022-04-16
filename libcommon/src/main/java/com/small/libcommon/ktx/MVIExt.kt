package com.small.libcommon.ktx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Created by small-ho on 2022/04 11:05
 * title: mvi ext
 */
fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> {
    return this
}
fun <T> SingleLiveEvents<T>.setEvent(vararg values: T) {
    this.value = values.toList()
}
fun <T> LiveData<List<T>>.observeEvent(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
    this.observe(lifecycleOwner) {
        it.forEach { event ->
            action.invoke(event)
        }
    }
}