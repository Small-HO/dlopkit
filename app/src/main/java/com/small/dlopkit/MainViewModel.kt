package com.small.dlopkit

import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.small.updatekit.UpdateNativeUtils
import kotlin.system.measureTimeMillis

/**
 * Created by small-ho on 2022/04 13:57
 * title:
 */
class MainViewModel: ViewModel() {

    private val suffix = "apk"

    private val path = Environment.getExternalStorageDirectory()

    private val oldFile = path.absolutePath + "/apk/" + "old.$suffix"
    private val newFile = path.absolutePath + "/apk/" + "new.$suffix"
    private val patchFile = path.absolutePath + "/apk/" + "ipc.patch"
    private val combineFile = path.absolutePath + "/apk/" + "combine.$suffix"

    /** 生成补丁文件 */
    fun fileDiff() {
        Thread {
            Log.i("------------>", "开始生成补丁文件...")
            val time = measureTimeMillis {
                UpdateNativeUtils.diff(newFile, oldFile, patchFile)
            }
            Log.i("------------>", "生成补丁文件耗时：$time")
        }.start()
    }

    /** 增量更新 */
    fun update() {
        Thread {
            Log.i("------------>", "开始合并补丁文件...")
            val time = measureTimeMillis {
                UpdateNativeUtils.patch(oldFile, patchFile, combineFile)
            }
            Log.i("------------>", "合并补丁耗时：$time")
        }.start()
    }

}