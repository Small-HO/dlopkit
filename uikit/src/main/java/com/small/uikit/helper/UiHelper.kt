package com.small.uikit.helper

import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * Created by small-ho on 2022/06 18:02
 * title: Ui帮助类
 */
interface UiHelper<T : BaseHelper<*>?> {
    var helper: T?
}

/** 背景色（px 单位属性与 XML 一致） */
fun UiHelper<*>.setBackgroundColorNormal(@ColorInt color: Int) {
    helper?.setBackgroundColorNormal(color)
}

/** 统一圆角，单位 px */
fun UiHelper<*>.setCornerRadius(@Px radius: Int) {
    helper?.setCornerRadius(radius)
}

/** 分别设置四角圆角，单位 px；顺序：左上、右上、左下、右下 */
fun UiHelper<*>.setCorners(
    @Px topLeft: Int,
    @Px topRight: Int,
    @Px bottomLeft: Int,
    @Px bottomRight: Int
) {
    helper?.setCorners(topLeft, topRight, bottomLeft, bottomRight)
}

/** 边框宽度与颜色，宽度单位 px */
fun UiHelper<*>.setBorder(@Px width: Int, @ColorInt color: Int) {
    helper?.setBorder(width, color)
}

/** 边框宽度，单位 px */
fun UiHelper<*>.setBorderWidth(@Px width: Int) {
    helper?.setBorderWidth(width)
}

/** 边框颜色 */
fun UiHelper<*>.setBorderColor(@ColorInt color: Int) {
    helper?.setBorderColor(color)
}
