package ru.sudox.android.messages

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.CoreController
import ru.sudox.android.messages.views.MessageItemView
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.people.common.vos.SimplePeopleVO

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        return MessageItemView(activity!!).apply {
//            Handler().postDelayed({
//                messageLikesView.addLike(SimplePeopleVO(1L, "name", 1L), glide)
//            }, 2000L)
//
//            Handler().postDelayed({
//                messageLikesView.addLike(SimplePeopleVO(1L, "name", 2L), glide)
//            }, 4000L)
//
//            Handler().postDelayed({
//                messageLikesView.addLike(SimplePeopleVO(1L, "name", 3L), glide)
//            }, 6000L)

            messageLikesView.setVOs(arrayListOf(
                    SimplePeopleVO(1L, "name", 1L),
                    SimplePeopleVO(1L, "name", 2L),
                    SimplePeopleVO(1L, "name", 3L),
                    SimplePeopleVO(1L, "MM", -1L)
            ), glide)
        }
    }
}