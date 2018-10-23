package com.sudox.android.ui.views.toolbar.expanded

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.util.AttributeSet
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.android.synthetic.main.view_status_expanded.view.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class StatusExpandedView : ExpandedView {

    @Inject
    lateinit var protocolClient: ProtocolClient

    // Observer for connection state data
    private var observer: Observer<ConnectionState> = Observer {
        if (it == ConnectionState.CONNECTION_CLOSED) {
            showMessage(context.getString(R.string.lost_internet_connection))
        } else if (it == ConnectionState.HANDSHAKE_SUCCEED) {
            showMessage(context.getString(R.string.connection_restored))
        }
    }

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

    override fun clear() {
        // Ignore
    }
}