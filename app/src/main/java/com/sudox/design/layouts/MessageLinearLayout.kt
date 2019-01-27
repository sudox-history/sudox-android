package com.sudox.design.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_dialog_message_to.view.*

class MessageLinearLayout : LinearLayout {

    private val TIME_MARGIN = (25 * resources.displayMetrics.density).toInt()
    private val MAX_WIDTH = resources.displayMetrics.widthPixels / 100 * 80

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Suppress("UNUSED_VARIABLE")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(MAX_WIDTH, View.MeasureSpec.AT_MOST), heightMeasureSpec)

        if (dialogMessageText.layout.lineCount >= 1) {
            val lastLineWidth = dialogMessageText.layout.getLineWidth(dialogMessageText.layout.lineCount - 1)
            val freeSpace = measuredWidth - lastLineWidth
            val neededSpace = dialogMessageSendTime.measuredWidth + TIME_MARGIN

            if (freeSpace >= neededSpace) {
                dialogMessageSendTime.translationY = (-dialogMessageSendTime.measuredHeight / 1.25F)

                // Remove parasitic bottom padding
                setMeasuredDimension(measuredWidth, (measuredHeight - (dialogMessageSendTime.measuredHeight)).toInt())
            } else if (dialogMessageText.layout.lineCount == 1 && measuredWidth + neededSpace <= MAX_WIDTH) {
                dialogMessageSendTime.translationY = (-dialogMessageSendTime.measuredHeight / 1.25F)

                // Remove parasitic bottom padding
                setMeasuredDimension(measuredWidth + neededSpace, (measuredHeight - (dialogMessageSendTime.measuredHeight)).toInt())
            } else {
                if (measuredWidth < neededSpace) {
                    dialogMessageSendTime.translationY = (-dialogMessageSendTime.measuredHeight / 1.25F)

                    // Remove parasitic bottom padding and add needed width
                    setMeasuredDimension(measuredWidth + neededSpace, (measuredHeight - (dialogMessageSendTime.measuredHeight)))
                } else {
                    if (lastLineWidth + neededSpace <= MAX_WIDTH) {
                        dialogMessageSendTime.translationY = (-dialogMessageSendTime.measuredHeight / 1.25F)

                        // Remove parasitic bottom padding and add needed width
                        setMeasuredDimension(measuredWidth + neededSpace, (measuredHeight - (dialogMessageSendTime.measuredHeight)))
                    } else {
                        dialogMessageSendTime.translationY = 0F

                        setMeasuredDimension(measuredWidth, ((measuredHeight - (dialogMessageSendTime.measuredHeight  * 0.20F)).toInt()))
                    }
                }
            }
        }
    }
}