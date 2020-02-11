package com.sudox.design.viewlist.header

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.common.createStyledView
import com.sudox.design.imagebutton.ImageButton
import com.sudox.design.popup.ListPopupWindow
import com.sudox.design.viewlist.R
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import kotlin.math.max

const val VIEW_LIST_HEADER_VIEW_TEXT_TAG = 1
const val VIEW_LIST_HEADER_VIEW_FUNCTION_BUTTON_TAG = 2

class ViewListHeaderView : ViewGroup, View.OnClickListener {

    private var togglePopupWindow: ListPopupWindow? = null

    var toggleIconDrawable: Drawable? = null
        set(value) {
            field = value?.mutate()?.apply {
                setBounds(0, 0, value.intrinsicWidth, value.intrinsicHeight)
                setTint(toggleIconTint)
            }

            vo = vo // Updating
        }

    var toggleIconTint = 0
        set(value) {
            toggleIconDrawable?.setTint(toggleIconTint)
            vo = vo // Updating
            field = value
        }

    var vo: ViewListHeaderVO? = null
        set(value) {
            if (value != null) {
                val toggleOptions = value.getToggleOptions(context)

                textView.tag = VIEW_LIST_HEADER_VIEW_TEXT_TAG
                textView.text = toggleOptions[value.selectedToggleTag].title
                textView.isClickable = toggleOptions.size > 1
                textView.setCompoundDrawables(null, null, if (toggleOptions.size > 1) {
                    toggleIconDrawable
                } else {
                    null
                }, null)

                val functionalButtonIcon = value.getFunctionButtonIcon(context)

                functionalImageButton!!.let {
                    it.iconDrawable = functionalButtonIcon
                            ?: if (value.canHideItems()) {
                                toggleIconDrawable
                            } else {
                                null
                            }

                    it.isClickable = functionalButtonIcon != null || value.canHideItems()
                    it.tag = VIEW_LIST_HEADER_VIEW_FUNCTION_BUTTON_TAG
                }
            }

            field = value
            requestLayout()
            invalidate()
        }

    private var functionalImageButton: ImageButton? = null
    private var textView = AppCompatTextView(context).apply {
        setOnClickListener(this@ViewListHeaderView)
        addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.viewListHeaderStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ViewListHeaderView, defStyleAttr, 0).use {
            setTextAppearance(textView, it.getResourceIdOrThrow(R.styleable.ViewListHeaderView_textAppearance))

            toggleIconTint = it.getColorOrThrow(R.styleable.ViewListHeaderView_toggleIconTint)
            toggleIconDrawable = it.getDrawableOrThrow(R.styleable.ViewListHeaderView_toggleIconDrawable)
            functionalImageButton = it.createStyledView<ImageButton>(context, R.styleable.ViewListHeaderView_functionalButtonStyle).apply {
                setOnClickListener(this@ViewListHeaderView)
                addView(this)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(textView, widthMeasureSpec, heightMeasureSpec)
        measureChild(functionalImageButton, widthMeasureSpec, heightMeasureSpec)

        val needWidth = MeasureSpec.getSize(widthMeasureSpec)
        val needHeight = paddingTop + paddingBottom + max(textView.measuredHeight, functionalImageButton!!.measuredHeight)

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val textLeftBorder = paddingLeft
        val textRightBorder = textLeftBorder + textView.measuredWidth
        val textTopBorder = measuredHeight / 2 - textView.measuredHeight / 2
        val textBottomBorder = textTopBorder + textView.measuredHeight

        textView.layout(textLeftBorder, textTopBorder, textRightBorder, textBottomBorder)

        val functionalButtonRightBorder = measuredWidth - paddingRight
        val functionalButtonLeftBorder = functionalButtonRightBorder - functionalImageButton!!.measuredWidth
        val functionalButtonTopBorder = measuredHeight / 2 - functionalImageButton!!.measuredHeight / 2
        val functionalButtonBottomBorder = functionalButtonTopBorder + functionalImageButton!!.measuredHeight

        functionalImageButton!!.layout(
                functionalButtonLeftBorder,
                functionalButtonTopBorder,
                functionalButtonRightBorder,
                functionalButtonBottomBorder
        )
    }

    override fun onClick(view: View) {
        if (view == textView) {
            togglePopupWindow?.dismiss()
            togglePopupWindow = ListPopupWindow(context, vo!!.getToggleOptions(context)) {
                vo!!.selectedToggleTag = it.tag
                togglePopupWindow!!.dismiss()
            }

            togglePopupWindow!!.showAsDropDown(textView)
//            togglePopupMenu?.dismiss()
//            togglePopupMenu = PopupMenu(context, textView)
//            togglePopupMenu!!.setOnMenuItemClickListener {
//                vo!!.selectedToggleIndex = it.itemId
//                true
//            }
//
//            for ((index, option) in vo?.getToggleOptions(context)!!.withIndex()) {
//                togglePopupMenu!!.menu.add(0, index, 0, "${option.second.first} ${if (index == vo!!.selectedToggleIndex) {
//                    "(Selected)"
//                } else {
//                    ""
//                }}")
//            }
//
//            togglePopupMenu!!.show()
        } else if (view == functionalImageButton) {
            if (vo!!.canHideItems()) {
                // TODO: Hide items
            } else {
                // TODO: Callback
            }
        }
    }
}