package com.sudox.design.navigation.toolbar.expanded

import android.content.Context
import android.util.AttributeSet
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.protocol.ProtocolClient
import kotlinx.android.synthetic.main.view_status_expanded.view.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class StatusExpandedView : ExpandedView {

    @Inject
    lateinit var protocolClient: ProtocolClient

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        turnBlackOverlay = false

        // Inflate view
        inflate(context, R.layout.view_status_expanded, this)

        // Inject all dependencies
        ApplicationLoader.component.inject(this)
    }

    fun showMessage(message: String, time: Long = 2500L) = GlobalScope.launch(Dispatchers.Main) {
        if (expanded) hide()

        // Update text
        statusExpandedViewText.text = message

        // Show
        show()

        // Auto-closing
        if (time > 0L) handler.postDelayed({ hide() }, time)
    }

    fun openWithMessage(message: String) {
        if (!expanded) {
            statusExpandedViewText.text = message
            show()
        }
    }

    fun close() {
        hide()
    }

    override fun clear() {
        // Ignore
    }
}