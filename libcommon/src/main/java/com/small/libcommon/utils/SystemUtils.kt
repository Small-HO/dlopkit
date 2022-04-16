package com.small.libcommon.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by small-ho on 2021/02 5:32 PM
 * title: 系统工具类
 */
object SystemUtils {

    /** ip 地址获取 */
    fun getGateWay(): String {
        val arr: Array<String>
        try {
            val process = Runtime.getRuntime().exec("ip route list table 0")
            val input = BufferedReader(InputStreamReader(process.inputStream))
            val string: String = input.readLine()
            arr = string.split("\\s+".toRegex()).toTypedArray()
            return arr[2]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "error"
    }

}