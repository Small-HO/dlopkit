package com.small.updatekit

/**
 * Created by small-ho on 2022/04 10:32
 * title: 增量更新本地接口
 */
object UpdateNativeUtils {

    init {
        System.loadLibrary("updatekit")
    }

    /** 生成补丁包 */
    external fun diff(newPath: String, oldPath: String, patchPath: String)

    /** 合并差分包 */
    external fun patch(oldPath: String, patch: String, combine: String)

}