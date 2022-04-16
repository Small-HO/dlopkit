package com.small.floatkit.common

import java.io.Serializable

/**
 * Created by small-ho on 2020/08 5:02 PM
 * title: 公共配置文件
 */
data class DlopConfig(
    var http_dev: String = "",
    var http_main: String = ""
): Serializable