package com.small.libcommon.ktx

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

/**
 * Created by small-ho on 2023/1/3 星期二 14:09
 * title: Serializable过期处理
 */
@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.serializable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
}
@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Intent.serializableExtra(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        getSerializableExtra(key) as? T
    }
}