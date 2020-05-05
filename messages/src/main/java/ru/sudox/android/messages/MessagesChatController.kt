package ru.sudox.android.messages

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.CoreController
import ru.sudox.android.messages.views.adapters.MessageLikesViewAdapter
import ru.sudox.android.messages.views.adapters.MessageLikesViewDecorator
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.people.common.vos.SimplePeopleVO

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        return RecyclerView(activity!!).apply {
            addItemDecoration(MessageLikesViewDecorator())

            itemAnimator!!.addDuration = 1500
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, true)
            adapter = MessageLikesViewAdapter(glide).apply {
                Handler().postDelayed({
                    firstLikesVOs.add(SimplePeopleVO(1L, "name", 1L))
                    notifyItemInserted(0)
                }, 2000L)

                Handler().postDelayed({
                    firstLikesVOs.add(SimplePeopleVO(2L, "name", 2L))
                    notifyItemInserted(0)
                }, 4000L)

                Handler().postDelayed({
                    firstLikesVOs.add(SimplePeopleVO(3L, "name", 3L))
                    notifyItemInserted(0)
                }, 6000L)

                Handler().postDelayed({
                    firstLikesVOs.add(SimplePeopleVO(4L, "name", 4L))
                    notifyItemInserted(3)
                }, 8000L)
            }
        }
    }
}