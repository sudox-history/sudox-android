package ru.sudox.android.messages

import android.os.Bundle
import android.os.Handler
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
//                messageLikesView.addVO(SimplePeopleVO(1L, "name", 1L), glide)
//            }, 2000L)

//            Handler().postDelayed({
//                messageLikesView.addVO(SimplePeopleVO(1L, "name", 2L), glide)
//                messageLikesView.addVO(SimplePeopleVO(1L, "name", 3L), glide)
//            }, 4000L)
//
            Handler().postDelayed({
                messageLikesView.setVOs(arrayListOf(
                        SimplePeopleVO(1L, "name", 1L)
                ), glide)
            }, 1000L)

            Handler().postDelayed({
                messageLikesView.setVOs(arrayListOf(
                        SimplePeopleVO(1L, "name", 1L),
                        SimplePeopleVO(1L, "name", 2L)
                ), glide)
            }, 3000L)

            Handler().postDelayed({
                messageLikesView.setVOs(arrayListOf(
                        SimplePeopleVO(1L, "name", 1L),
                        SimplePeopleVO(1L, "name", 2L),
                        SimplePeopleVO(1L, "name", 3L)
                ), glide)
            }, 5000L)

            Handler().postDelayed({
                messageLikesView.setVOs(arrayListOf(
                        SimplePeopleVO(1L, "name", 1L),
                        SimplePeopleVO(1L, "name", 2L),
                        SimplePeopleVO(1L, "name", 3L),
                        SimplePeopleVO(1L, "name", 3L)
                ), glide)
            }, 7000L)

            Handler().postDelayed({
                messageLikesView.setVOs(arrayListOf(
                        SimplePeopleVO(1L, "name", 1L),
                        SimplePeopleVO(1L, "name", 2L),
                        SimplePeopleVO(1L, "name", 3L)
                ), glide)
            }, 9000L)

            Handler().postDelayed({
                messageLikesView.setVOs(arrayListOf(
                        SimplePeopleVO(1L, "name", 1L),
                        SimplePeopleVO(1L, "name", 2L)
                ), glide)
            }, 11000L)

            Handler().postDelayed({
                messageLikesView.setVOs(arrayListOf(
                        SimplePeopleVO(1L, "name", 1L)
                ), glide)
            }, 13000L)

            Handler().postDelayed({
                messageLikesView.setVOs(null, glide)
            }, 15000L)
        }
    }
}