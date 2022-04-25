package com.small.videokit

import android.view.Surface

/**
 * Created by small-ho on 2022/04 10:09
 * title: 加载动态库
 */
object FFmpegNativeUtils {

    init {
        System.loadLibrary("native-lib")
    }

    external fun videoPlay(path: String, surface: Surface)

}