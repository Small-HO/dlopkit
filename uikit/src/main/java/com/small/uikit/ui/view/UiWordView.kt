package com.small.uikit.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.small.uikit.R
import com.small.uikit.utils.WordUtils

/**
 * Created by small-ho on 2022/07 17:40
 * title: 自定义取词选择 textView
 */
class UiWordView : AppCompatTextView {

    private var mBackgroundNormal = 0
    private var mCornerRadius = -1
    private var mCornerRadiusTopLeft = 0
    private var mCornerRadiusTopRight = 0
    private var mCornerRadiusBottomLeft = 0
    private var mCornerRadiusBottomRight = 0
    private var mBorderWidth = 0
    private var mBorderColor = 0

    private var mIsCheck = false
    private var mErrorColor = 0
    private var mSucceedColor = 0
    private var mErrorText: String? = null
    private var mSucceedText: String? = null

    private var mString: SpannableString? = null
    private var mType: BufferType? = null

    private val mMap: MutableMap<String, Int> = mutableMapOf()

    private var bold = StyleSpan(Typeface.BOLD)
    private var textColor = ForegroundColorSpan(Color.BLACK)
    private var textBackground = BackgroundColorSpan(Color.parseColor("#DDE0E4"))


    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initAttributeSet(context, attrs)
        initDrawableSet()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UiWordView)
        //  背景
        mBackgroundNormal = typedArray.getColor(R.styleable.UiWordView_background_normal, mBackgroundNormal)
        //  角度
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.UiWordView_corner_radius, mCornerRadius)
        mCornerRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.UiWordView_corner_radius_top_left, mCornerRadiusTopLeft)
        mCornerRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.UiWordView_corner_radius_top_right, mCornerRadiusTopRight)
        mCornerRadiusBottomLeft = typedArray.getDimensionPixelSize(R.styleable.UiWordView_corner_radius_bottom_left, mCornerRadiusBottomLeft)
        mCornerRadiusBottomRight = typedArray.getDimensionPixelSize(R.styleable.UiWordView_corner_radius_bottom_right, mCornerRadiusBottomRight)
        //  边框
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.UiWordView_border_width, mBorderWidth)
        mBorderColor = typedArray.getColor(R.styleable.UiWordView_border_color, mBorderColor)
        //  多颜色
        mIsCheck = typedArray.getBoolean(R.styleable.UiWordView_is_check, mIsCheck)
        mErrorColor = typedArray.getColor(R.styleable.UiWordView_error_color, mErrorColor)
        mSucceedColor = typedArray.getColor(R.styleable.UiWordView_succeed_color, mSucceedColor)
        mErrorText = typedArray.getString(R.styleable.UiWordView_error_text)
        mSucceedText = typedArray.getString(R.styleable.UiWordView_succeed_text)
        typedArray.recycle()
    }

    private fun initDrawableSet() {
        val shape = GradientDrawable()
        //  颜色
        if (mBackgroundNormal != 0) { shape.setColor(mBackgroundNormal) }
        //  角度
        if (mCornerRadius > 0) {
            shape.cornerRadius = mCornerRadius.toFloat()
        } else {
            shape.cornerRadii = floatArrayOf(
                mCornerRadiusTopLeft.toFloat(), mCornerRadiusTopLeft.toFloat(),
                mCornerRadiusTopRight.toFloat(), mCornerRadiusTopRight.toFloat(),
                mCornerRadiusBottomLeft.toFloat(), mCornerRadiusBottomLeft.toFloat(),
                mCornerRadiusBottomRight.toFloat(), mCornerRadiusBottomRight.toFloat()
            )
        }
        //  边框
        if (mBorderWidth != 0 || mBorderColor != 0) { shape.setStroke(mBorderWidth, mBorderColor) }
        background = shape
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        this.mType = type
        movementMethod = LinkMovementMethod.getInstance()
        if (mString.isNullOrEmpty()) {
            mString = SpannableString(text)
        }
        mString?.let { initTextColor(text, it, mType) }
        super.setText(mString, mType)
    }

    /** 清除操作 */
    fun clear() {
        mString?.let {
            it.removeSpan(bold)
            it.removeSpan(textColor)
            it.removeSpan(textBackground)
            super.setText(mString, mType)
        }
        mMap.clear()
        mWordClickListener?.let { it("", 0, 0) }
    }

    private fun initTextColor(text: CharSequence?, string: SpannableString, type: BufferType?) {
        val words = WordUtils.getEnglishWordIndices(text.toString())
        words.forEach { wordInfo ->
            if (mErrorText != null) {
                mErrorText?.let {
                    if (it.contains(text?.substring(wordInfo.start, wordInfo.end).toString())) {
                        string.setSpan(ForegroundColorSpan(mErrorColor),wordInfo.start, wordInfo.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        string.setSpan(StyleSpan(Typeface.BOLD),wordInfo.start, wordInfo.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            if (mSucceedText != null) {
                mSucceedText?.let {
                    if (it.contains(text?.substring(wordInfo.start, wordInfo.end).toString())) {
                        string.setSpan(ForegroundColorSpan(mSucceedColor),wordInfo.start, wordInfo.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        string.setSpan(StyleSpan(Typeface.BOLD),wordInfo.start, wordInfo.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            if (mIsCheck) {
                string.setSpan(getClickableSpan(string, type), wordInfo.start, wordInfo.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }



    private var mWordClickListener: ((String, Int, Int) -> Unit)? = null
    fun setOnWordClickListener(listener: (String, Int, Int) -> Unit) { this.mWordClickListener = listener }

    private fun getClickableSpan(string: SpannableString, type: BufferType?): ClickableSpan {
        return object : ClickableSpan() {
            override fun onClick(widget: View) {
                val tv = widget as TextView
                val start = tv.selectionStart
                val end = tv.selectionEnd
                if (TextUtils.isEmpty(text) || start == -1 || end == -1) {
                    return
                }
                setSelectedSpan(tv,string, type)
            }
            override fun updateDrawState(ds: TextPaint) {}
        }
    }

    /** 设置点击颜色 */
    private fun setSelectedSpan(tv: TextView, string: SpannableString, type: BufferType?) {
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
        string.removeSpan(textColor)
        string.removeSpan(textBackground)
        if (mMap.isNotEmpty()) {
            string.setSpan(bold, mMap.getValue("start"), mMap.getValue("end"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            string.setSpan(textColor, mMap.getValue("start"), mMap.getValue("end"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            string.setSpan(textBackground, mMap.getValue("start"), mMap.getValue("end"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val word = tv.text.subSequence(mMap.getValue("start"), mMap.getValue("end")).toString()
            mWordClickListener?.let { it(word, mMap.getValue("start"), mMap.getValue("end")) }
        }else {
            mWordClickListener?.let { it("", 0, 0) }
        }
        super.setText(string,type)
    }

}