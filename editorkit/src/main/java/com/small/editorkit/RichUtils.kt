package com.small.editorkit

import android.text.TextUtils
import java.util.ArrayList
import java.util.regex.Pattern

/**
 * Created by small-ho on 2022/06 14:35
 * title:
 */
object RichUtils {
    /**
     * 截取富文本中的图片链接
     */
    fun returnImageUrlsFromHtml(content: String): ArrayList<String?> {
        val imageSrcList: MutableList<String?> = ArrayList()
        if (TextUtils.isEmpty(content)) {
            return imageSrcList as ArrayList<String?>
        }
        val p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE)
        val m = p.matcher(content)
        var quote: String?
        var src: String?
        while (m.find()) {
            quote = m.group(1)
            src = if (quote == null || quote.trim { it <= ' ' }.isEmpty()) m.group(2).split("//s+".toRegex()).toTypedArray()[0] else m.group(2)
            imageSrcList.add(src)
        }
        return imageSrcList as ArrayList<String?>
    }

    /**
     * 截取富文本中的纯文本内容
     */
    fun returnOnlyText(htmlStr: String): String {
        return if (TextUtils.isEmpty(htmlStr)) {
            ""
        } else {
            val regFormat = "\\s*|\t|\r|\n"
            val regTag = "<[^>]*>"
            var text = htmlStr.replace(regFormat.toRegex(), "").replace(regTag.toRegex(), "")
            text = text.replace("&nbsp;", "")
            text
        }
    }

    fun isEmpty(htmlStr: String): Boolean {
        val images = returnImageUrlsFromHtml(htmlStr)
        val text = returnOnlyText(htmlStr)
        return TextUtils.isEmpty(text) && images.size == 0
    }
}