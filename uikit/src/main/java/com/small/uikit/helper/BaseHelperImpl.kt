package com.small.uikit.helper

import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * Created by small-ho on 2022/06 16:10
 * title: 帮助类接口
 */
interface BaseHelperImpl {
    fun setBackgroundColorNormal(@ColorInt color: Int): BaseHelper<*>
    fun setCornerRadius(@Px radius: Int): BaseHelper<*>
    fun setCorners(
        @Px topLeft: Int,
        @Px topRight: Int,
        @Px bottomLeft: Int,
        @Px bottomRight: Int
    ): BaseHelper<*>
    fun setBorder(@Px width: Int, @ColorInt color: Int): BaseHelper<*>
    fun setBorderWidth(@Px width: Int): BaseHelper<*>
    fun setBorderColor(@ColorInt color: Int): BaseHelper<*>
}
