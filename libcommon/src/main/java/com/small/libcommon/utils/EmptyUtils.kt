package com.small.libcommon.utils

/**
 * Created by small-ho on 2021/01 4:42 PM
 * title: 判空工具类
 */
object EmptyUtils {
    //判空
    fun isEmpty(obj: Any?): Boolean {
        if (obj == "") return true
        if (obj == 0) return true
        if (obj == 0.0) return true
        if (obj == null) return true
        if (obj is CharSequence) return obj.length == 0
        if (obj is Collection<*>) return obj.isEmpty()
        if (obj is Map<*, *>) return obj.isEmpty()
        if (obj is Array<*>) {
            if (obj.size == 0) {
                return true
            }
            var empty = true
            for (i in obj.indices) {
                if (!isEmpty(obj[i])) {
                    empty = false
                    break
                }
            }
            return empty
        }
        return false
    }

    fun isNotEmpty(obj: Any?): Boolean {
        return !isEmpty(obj)
    }

    private fun validPropertyEmpty(vararg args: Any): Boolean {
        for (element in args) {
            if (isEmpty(element)) {
                return true
            }
        }
        return false
    }
}