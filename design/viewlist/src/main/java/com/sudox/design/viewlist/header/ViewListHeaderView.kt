package com.sudox.design.viewlist.header

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.common.createStyledView
import com.sudox.design.imagebutton.ImageButton
import com.sudox.design.popup.ListPopupController
import com.sudox.design.popup.ListPopupWindow
import com.sudox.design.viewlist.R
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import kotlin.math.max

const val VIEW_LIST_HEADER_VIEW_TEXT_TAG = 1
const val VIEW_LIST_HEADER_VIEW_FUNCTION_BUTTON_TAG = 2

// 5000 = 180 градусов (10000 - 360 градусов)
const val LEVEL_OF_ROTATED_DRAWABLE = 5000

class ViewListHeaderView : ViewGroup, View.OnClickListener {

    private var showingAnimationDuration = 0L
    private var hidingAnimationDuration = 0L

    private var visibilityChangingAnimator = ValueAnimator.ofInt(0, LEVEL_OF_ROTATED_DRAWABLE).apply {
        addUpdateListener { listener ->
            functionalImageButton!!.let {
                // P.S.: RotateDrawable будет задан от toggleIconDrawable при его мутации
                (it.iconDrawable as RotateDrawable).level = listener.animatedValue as Int
                it.invalidate()
            }
        }
    }

    private var sectionChangingPopupController = ListPopupController().apply {
        togglingAnimator = ValueAnimator.ofInt(0, LEVEL_OF_ROTATED_DRAWABLE).apply {
            addUpdateListener {
                if (anchorView == textView) {
                    toggleIconDrawable!!.level = it.animatedValue as Int
                }
            }
        }
    }

    var toggleIconDrawable: Drawable? = null
        set(value) {
            if (value != null) {
                field = RotateDrawable().apply {
                    drawable = value.mutate()

                    setBounds(0, 0, value.intrinsicWidth, value.intrinsicHeight)
                    setTint(toggleIconTint)
                }
            }

            vo = vo // Подгоняем данные под новую иконку
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
                textView.text = toggleOptions.find { it.tag == value.selectedToggleTag }!!.title
                textView.isClickable = toggleOptions.size > 1 || value.canHideItems()
                textView.setCompoundDrawables(null, null, if (toggleOptions.size > 1) {
                    toggleIconDrawable
                } else {
                    null
                }, null)

                val functionalButtonIcon = value.getFunctionButtonIcon(context)

                if (value.isItemsHidden) {
                    toggleIconDrawable!!.level = LEVEL_OF_ROTATED_DRAWABLE
                }

                functionalImageButton!!.let {
                    // Не вызываем drawable.mutate(), т.к. оно уже будет вызвано изнутри ImageButton
                    it.iconDrawable = functionalButtonIcon ?: if (value.canHideItems()) {
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

    var sortTypeChangingCallback: ((ViewListHeaderVO, Int) -> (Unit))? = null
    var itemsVisibilityTogglingCallback: ((ViewListHeaderVO) -> (Unit))? = null
    var itemsSectionChangingCallback: ((ViewListHeaderVO, Int) -> (Unit))? = null
    var getItemsCountBeforeChanging: ((ViewListHeaderVO) -> (Int))? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.viewListHeaderStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ViewListHeaderView, defStyleAttr, 0).use {
            setTextAppearance(textView, it.getResourceIdOrThrow(R.styleable.ViewListHeaderView_textAppearance))

            toggleIconTint = it.getColorOrThrow(R.styleable.ViewListHeaderView_toggleIconTint)
            toggleIconDrawable = it.getDrawableOrThrow(R.styleable.ViewListHeaderView_toggleIconDrawable)
            functionalImageButton = it.createStyledView<ImageButton>(context, R.styleable.ViewListHeaderView_functionalButtonStyle)!!.apply {
                setOnClickListener(this@ViewListHeaderView)
                this@ViewListHeaderView.addView(this)
            }

            sectionChangingPopupController
                    .togglingAnimator!!
                    .duration = it.getIntegerOrThrow(R.styleable.ViewListHeaderView_iconAnimationDuration).toLong()
        }
    }

    /**
     * Синхронизирует длительность анимаций с ViewList
     * Необходимо вызвать если используются анимации скрытия контента
     *
     * @param viewList ViewList с которым нужно синхронизироваться
     */
    fun syncWithViewList(viewList: ViewList) {
        showingAnimationDuration = viewList.itemAnimator?.addDuration!!
        hidingAnimationDuration = viewList.itemAnimator?.removeDuration!!
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
        val textTopBorder = paddingTop
        val textBottomBorder = textTopBorder + textView.measuredHeight

        textView.layout(textLeftBorder, textTopBorder, textRightBorder, textBottomBorder)

        val functionalButtonRightBorder = measuredWidth - paddingRight
        val functionalButtonLeftBorder = functionalButtonRightBorder - functionalImageButton!!.measuredWidth
        val functionalButtonTopBorder = paddingTop
        val functionalButtonBottomBorder = functionalButtonTopBorder + functionalImageButton!!.measuredHeight

        functionalImageButton!!.layout(
                functionalButtonLeftBorder,
                functionalButtonTopBorder,
                functionalButtonRightBorder,
                functionalButtonBottomBorder
        )
    }

    override fun onClick(view: View) {
        val toggleOptions = vo!!.getToggleOptions(context)
        val functionalButtonsOptions = vo!!.getFunctionButtonToggleOptions(context)

        if (view == textView && toggleOptions.size > 1) {
            sectionChangingPopupController.show(ListPopupWindow(context, toggleOptions, true) {
                val itemsCount = getItemsCountBeforeChanging!!(vo!!)

                vo!!.selectedToggleTag = it.tag
                itemsSectionChangingCallback!!(vo!!, itemsCount)
                vo = vo // Обновляем данные ...
            }, parent as View, textView, Gravity.START, true)
        } else if (!vo!!.canHideItems() && functionalButtonsOptions!!.size > 1) {
            sectionChangingPopupController.show(ListPopupWindow(context, functionalButtonsOptions, true) {
                vo!!.selectFunctionalToggleTag(it.tag)

                if (vo!!.canSortItems()) {
                    sortTypeChangingCallback!!(vo!!, getItemsCountBeforeChanging!!(vo!!))
                }
            }, parent as View, functionalImageButton!!, Gravity.END, true)
        } else {
            if (vo!!.isItemsHidden) {
                visibilityChangingAnimator.let {
                    it.duration = hidingAnimationDuration
                    it.reverse()
                }

                vo!!.isItemsHidden = false
            } else {
                visibilityChangingAnimator.let {
                    it.duration = showingAnimationDuration
                    it.start()
                }

                vo!!.isItemsHidden = true
            }

            itemsVisibilityTogglingCallback!!(vo!!)
        }
    }
}