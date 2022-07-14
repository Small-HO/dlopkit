package com.small.uikit.utils

import java.util.*

data class WordInfo(var start: Int = 0, var end: Int = 0)
/**
 * Created by small-ho on 2022/07 18:02
 * title: 取词控件工具类
 */
object WordUtils {

    private var sPunctuations: List<Char> = arrayListOf()

    init {
        val arr = arrayOf(',', '.', ';', '!', '"', '，', '。', '！', '；', '、', '：', '“', '”', '?', '？')
        sPunctuations = arr.asList()
    }

    fun isChinese(ch: Char): Boolean = !sPunctuations.contains(ch)

    fun getEnglishWordIndices(content: String): MutableList<WordInfo> {
        val separatorIndices: MutableList<Int> = getSeparatorIndices(content, ' ')
        for (punctuation in sPunctuations) {
            separatorIndices.addAll(getSeparatorIndices(content, punctuation))
        }
        separatorIndices.sort()
        val wordInfoList: MutableList<WordInfo> = ArrayList<WordInfo>()
        var start = 0
        var end: Int
        for (i in separatorIndices.indices) {
            end = separatorIndices[i]
            if (start == end) {
                start++
            } else {
                val wordInfo = WordInfo()
                wordInfo.start = start
                wordInfo.end = end
                wordInfoList.add(wordInfo)
                start = end + 1
            }
        }
        return wordInfoList
    }

    private fun getSeparatorIndices(word: String, ch: Char): MutableList<Int> {
        var pos = word.indexOf(ch)
        val indices: MutableList<Int> = ArrayList()
        while (pos != -1) {
            indices.add(pos)
            pos = word.indexOf(ch, pos + 1)
        }
        return indices
    }
}