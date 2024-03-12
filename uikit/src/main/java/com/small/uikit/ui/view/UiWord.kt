package com.small.uikit.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import com.small.uikit.R
import com.small.uikit.utils.WordUtils


/**
 * Created by small-ho on 2023/10/18 14:27
 * title:
 */
class UiWord: AppCompatTextView {

    private var mTxtPaint: Paint? = null                            // 背景色画笔
    private var mTxtRect: Rect? = null                              // 背景
    private var mTxtBgColor = Color.parseColor("#FFF4E6") // 文本背景色
    private var mTxtBackgroundCheck = false                         // 文本背景色是否开启

    private var mType: BufferType? = null
    private var mString: SpannableString? = null
    private var textColor = Color.parseColor("#0476E9")    // 文本颜色

    private var mTxtLineColor = Color.parseColor("#1F8FFF")// 下划线颜色

    private var mIsCheck = true
    private var mChoiceType = false
    private val mMap: MutableMap<String, Int> = mutableMapOf()

    private var bold = StyleSpan(Typeface.BOLD)
    private var textBackground = BackgroundColorSpan(Color.parseColor("#DDE0E4"))

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initAttributeSet(context, attrs)
        initPaintSet()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UiWordView)
        mTxtBgColor = typedArray.getColor(R.styleable.UiWordView_txt_bg_color, mTxtBgColor)
        mTxtBackgroundCheck = typedArray.getBoolean(R.styleable.UiWordView_txt_bg_check, mTxtBackgroundCheck)
        typedArray.recycle()
    }

    /** 文本背景绘制初始化 */
    private fun initPaintSet() {
        mTxtPaint = Paint()
        mTxtRect = Rect()

    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        this.mType = type
        movementMethod = LinkMovementMethod.getInstance()
        mString = SpannableString(text)
        mString?.let { initTextColor(text, it, mType) }
        super.setText(mString, mType)
    }

    override fun onDraw(canvas: Canvas) {
        drawTxtBackground(canvas)
        super.onDraw(canvas)
    }

    /** 绘制文本背景 */
    private fun drawTxtBackground(canvas: Canvas?) {
        if (mTxtBackgroundCheck) {
            mTxtPaint?.color = mTxtBgColor
            val lineCount = layout.lineCount
            for (i in 0 until lineCount) {
                mTxtRect?.let {
                    it.top = layout.getLineTop(i)
                    it.left = layout.getLineLeft(i).toInt()
                    it.right = layout.getLineRight(i).toInt()
                    it.bottom = layout.getLineBottom(i) - if (i + 1 == lineCount) 0 else layout.spacingAdd.toInt()
                }
                mTxtRect?.let { mTxtPaint?.let { it1 -> canvas?.drawRect(it, it1) } }
            }
        }else {
            mTxtPaint?.reset()
        }
    }

    /** 设置生词显示隐藏 */
    fun setWordColor(txt: MutableList<String> = mutableListOf(), type: Boolean = false) {
        if (txt.isEmpty()) { return }
        val words = WordUtils.getEnglishWordIndices(text.toString())
        mString?.let {string ->
            words.forEach { wordInfo ->
                if (txt.contains(text?.substring(wordInfo.start, wordInfo.end).toString())) {
                    string.setSpan(object : UnderlineSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = if (type) textColor else currentTextColor
                        }
                    }, wordInfo.start, wordInfo.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }
        super.setText(mString, mType)
    }

    /** 设置下划线显示隐藏 */
    fun setWordUnderline(txt: MutableList<String> = mutableListOf(), type: Boolean = false) {
        if (txt.isEmpty()) { return }
        val words = WordUtils.getEnglishWordIndices(text.toString())
        txt.forEach { items ->
            val item = WordUtils.getEnglishWordIndices(items)
            val length = items.length
            mString?.let { string ->
                words.forEach { word ->
                    if (item.size > 0) {
                        if (items.subSequence(item[0].start,item[0].end).toString() == text?.substring(word.start, word.end).toString()) {
                            if (items == text?.substring(word.start, word.start + length).toString()) {
                                string.setSpan(object : UnderlineSpan() {
                                    @RequiresApi(Build.VERSION_CODES.Q)
                                    override fun updateDrawState(ds: TextPaint) {
                                        ds.underlineColor = mTxtLineColor
                                        ds.underlineThickness = if (type) 3f else 0f
                                    }}, word.start, word.start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                    }else {
                        if (items == text?.substring(word.start, word.end).toString()) {
                            string.setSpan(object : UnderlineSpan() {
                                @RequiresApi(Build.VERSION_CODES.Q)
                                override fun updateDrawState(ds: TextPaint) {
                                    ds.underlineColor = mTxtLineColor
                                    ds.underlineThickness = if (type) 3f else 0f
                                }}, word.start, word.start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            }
        }
        super.setText(mString, mType)
    }

    /** 设置加粗显示隐藏 */
    fun setWordPhrases(txt: MutableList<String> = mutableListOf(), type: Boolean = false) {
        if (txt.isEmpty()) { return }
        val words = WordUtils.getEnglishWordIndices(text.toString())
        txt.forEach { items ->
            val item = WordUtils.getEnglishWordIndices(items)
            val length = items.length
            mString?.let { string ->
                words.forEach { word ->
                    if (item.size > 0) {
                        if (items.subSequence(item[0].start,item[0].end).toString() == text?.substring(word.start, word.end).toString()) {
                            if (items == text?.substring(word.start, word.start + length).toString()) {
                                string.setSpan(object : UnderlineSpan() {
                                    override fun updateDrawState(ds: TextPaint) {
                                        ds.isFakeBoldText = type
                                    }}, word.start, word.start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                    }else {
                        if (items == text?.substring(word.start, word.end).toString()) {
                            string.setSpan(object : UnderlineSpan() {
                                override fun updateDrawState(ds: TextPaint) {
                                    ds.isFakeBoldText = type
                                }}, word.start, word.start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            }
        }
        super.setText(mString, mType)
    }

    /** 设置背景显示隐藏 */
    fun setWordBackground(type: Boolean = false) {
        mTxtBackgroundCheck = type
        invalidate()
    }


    private fun initTextColor(text: CharSequence?, string: SpannableString, type: BufferType?) {
        val words = WordUtils.getEnglishWordIndices(text.toString())
        words.forEach { wordInfo ->
            if (mIsCheck) {
                string.setSpan(getClickableSpan(string, type), wordInfo.start, wordInfo.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private var mWordClickListener: ((String, Int, Int, IntArray, TextView) -> Unit)? = null
    fun setOnWordClickListener(listener: (String, Int, Int, IntArray, TextView) -> Unit) { this.mWordClickListener = listener }

    private fun getClickableSpan(string: SpannableString, type: BufferType?): ClickableSpan {
        return object : ClickableSpan() {
            override fun onClick(widget: View) {
                val tv = widget as TextView
                val start = tv.selectionStart
                val end = tv.selectionEnd
                if (TextUtils.isEmpty(text) || start == -1 || end == -1) {
                    return
                }
                val array = IntArray(4)
                measurePosition(this, widget, array)
                if (!mChoiceType) { mMap.clear() }
                setSelectedSpan(tv,string, type, array)
            }
            override fun updateDrawState(ds: TextPaint) {}
        }
    }

    /** 设置点击颜色 */
    private fun setSelectedSpan(tv: TextView, string: SpannableString, type: BufferType?, array: IntArray) {
        if (mMap.isEmpty()) {
            mMap["start"] = tv.selectionStart
            mMap["end"] = tv.selectionEnd
        }else {
            when {
                mMap.getValue("start") < tv.selectionStart -> {
                    if (mMap.getValue("end") != tv.selectionEnd) {
                        mMap["end"] = tv.selectionEnd
                    }else {
                        mMap["end"] = tv.selectionStart - 1
                    }
                }
                mMap.getValue("start") == tv.selectionStart -> {
                    if (mMap.getValue("end") != tv.selectionEnd) {
                        mMap["start"] = tv.selectionStart
                        mMap["end"] = tv.selectionEnd
                    }else {
                        mMap.clear()
                    }
                }
                mMap.getValue("start") > tv.selectionStart -> mMap["start"] = tv.selectionStart
            }
        }
        string.removeSpan(bold)
        string.removeSpan(textBackground)
        if (mMap.isNotEmpty()) {
            string.setSpan(bold, mMap.getValue("start"), mMap.getValue("end"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            string.setSpan(textBackground, mMap.getValue("start"), mMap.getValue("end"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val word = tv.text.subSequence(mMap.getValue("start"), mMap.getValue("end")).toString()
            mWordClickListener?.let { it(word, mMap.getValue("start"), mMap.getValue("end"), array, tv) }
        }else {
            mWordClickListener?.let { it("", 0, 0, array, tv) }
        }
        super.setText(string,type)
    }

    /** 位置测量 */
    fun measurePosition(spanned: ClickableSpan?, widget: View, array: IntArray) {
        val parentTextView = widget as TextView
        val parentTextViewRect = Rect()
        // 初始化计算点击View位置的值
        val completeText = parentTextView.text as SpannableString
        val textViewLayout = parentTextView.layout
        val startOffsetOfClickedText = completeText.getSpanStart(spanned)
        val endOffsetOfClickedText = completeText.getSpanEnd(spanned)
        val startXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal(startOffsetOfClickedText)
        val endXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal(endOffsetOfClickedText)
        // 获得点击文本的矩形
        val currentLineStartOffset = textViewLayout.getLineForOffset(startOffsetOfClickedText)
        val currentLineEndOffset = textViewLayout.getLineForOffset(endOffsetOfClickedText)
        val keywordIsInMultiLine = currentLineStartOffset != currentLineEndOffset
        textViewLayout.getLineBounds(currentLineStartOffset, parentTextViewRect)
        // 将矩形位置更新为他在屏幕上的真实位置
        val parentTextViewLocation = IntArray(2)
        parentTextView.getLocationOnScreen(parentTextViewLocation)
        val parentTextViewTopAndBottomOffset = parentTextViewLocation[1] - parentTextView.scrollY + parentTextView.compoundPaddingTop
        parentTextViewRect.top += parentTextViewTopAndBottomOffset
        parentTextViewRect.bottom += parentTextViewTopAndBottomOffset
        parentTextViewRect.left += (parentTextViewLocation[0] + startXCoordinatesOfClickedText + parentTextView.compoundPaddingLeft - parentTextView.scrollX).toInt()
        parentTextViewRect.right = (parentTextViewRect.left + endXCoordinatesOfClickedText - startXCoordinatesOfClickedText).toInt()
        var x = parentTextViewRect.left
        val y = parentTextViewRect.bottom
        if (keywordIsInMultiLine) {
            x = parentTextViewRect.left
        }
        array[0] = x
        array[1] = y
        array[2] = parentTextViewRect.width()
        array[3] = parentTextViewRect.height()
    }
}