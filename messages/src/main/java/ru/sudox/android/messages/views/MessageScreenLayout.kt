package ru.sudox.android.messages.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.res.use
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sudox.android.messages.R
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListContainer

/**
 * View для отображения контента экрана сообщений
 */
class MessageScreenLayout : ViewGroup {

    private var listPaddingVertical = 0
    private var listPaddingHorizontal = 0
    private val viewListContainer = ViewListContainer(context).apply {
        this@MessageScreenLayout.addView(this)
    }

    private val sendLayout = MessageSendLayout(context).apply {
        this@MessageScreenLayout.addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageScreenLayoutStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MessageScreenLayout, defStyleAttr, 0).use {
            listPaddingHorizontal = it.getDimensionPixelSize(R.styleable.MessageScreenLayout_listPaddingHorizontal, 0)
            listPaddingVertical = it.getDimensionPixelSize(R.styleable.MessageScreenLayout_listPaddingVertical, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(sendLayout, widthMeasureSpec, heightMeasureSpec)

        val listHeight = MeasureSpec.getSize(heightMeasureSpec) -
                sendLayout.measuredHeight -
                paddingBottom -
                paddingTop

        viewListContainer.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(listHeight, MeasureSpec.EXACTLY))
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val listTop = paddingTop
        val listLeft = paddingLeft
        val listBottom = listTop + viewListContainer.measuredHeight
        val listRight = listLeft + viewListContainer.measuredWidth

        viewListContainer.layout(listLeft, listTop, listRight, listBottom)

        val sendBottom = listBottom + sendLayout.measuredHeight
        val sendLeft = paddingLeft
        val sendRight = sendLeft + sendLayout.measuredWidth

        sendLayout.layout(sendLeft, listBottom, sendRight, sendBottom)
    }

    /**
     * Устанавливает адаптер для ViewList'а
     * Также настраивает адаптер на заполнение списка снизу-вверх.
     *
     * @param adapter Адаптер, который нужно использовать.
     */
    fun setAdapter(adapter: ViewListAdapter<*>) {
        viewListContainer.viewList = ViewList(context).also {
            adapter.viewList = it

            it.setPadding(listPaddingHorizontal, listPaddingVertical, listPaddingHorizontal, listPaddingVertical)
            it.setHasFixedSize(true)

            it.adapter = adapter
            it.itemAnimator = null
            it.layoutAnimation = null
            it.layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
            it.isNestedScrollingEnabled = false
            it.clipToPadding = false
        }
    }
}