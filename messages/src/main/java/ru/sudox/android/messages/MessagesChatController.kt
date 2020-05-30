package ru.sudox.android.messages

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.CoreController
import ru.sudox.android.media.vos.impls.ImageAttachmentVO
import ru.sudox.android.messages.adapters.MessagesAdapter
import ru.sudox.android.messages.views.MessageScreenLayout
import ru.sudox.android.messages.vos.MessagesChatVO
import ru.sudox.android.messages.vos.appbar.BaseMessagesAppBarVO
import ru.sudox.android.messages.vos.messages.ChatMessageVO
import ru.sudox.android.people.common.vos.SimplePeopleVO

const val MESSAGES_CONTROLLER_DIALOG_ID_KEY = "dialog_id"

class MessagesChatController : CoreController(true) {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return MessageScreenLayout(activity!!)
    }

    override fun bindView(view: View) {
        super.bindView(view)

        appBarVO = BaseMessagesAppBarVO<MessagesChatVO>(glide).apply {
            vo = MessagesChatVO(1L, "Maxim Mityushkin", 0L, 4L)
        }

        val adapter = MessagesAdapter(glide, activity!!)

        if (view is MessageScreenLayout) {
            view.setAdapter(adapter)
        }

        adapter.insertNewMessage(ChatMessageVO("1", "Здаров!", "1", null, null, false, System.currentTimeMillis()))
        adapter.insertNewMessage(ChatMessageVO("2", "Я тут свою M760Li зачиповал до 950 сил. Не хочешь посмотреть?", "1", null, null, false, System.currentTimeMillis()))
        adapter.insertNewMessage(ChatMessageVO("3", null, "1", arrayListOf(ImageAttachmentVO(8L).apply {
            height = 1800
            width = 2880
        }), null, false, System.currentTimeMillis()))

        adapter.insertNewMessage(ChatMessageVO("4", "Макс, сорри, но я сейчас занят.", "2", null, arrayListOf(
                SimplePeopleVO(1L, "TheMax", 4L)
        ), true, System.currentTimeMillis() + 60000L))

        adapter.insertNewMessage(ChatMessageVO("5", "Сейчас точно не время, давай завтра.", "2", null, null, true, System.currentTimeMillis() + 60000L))
        adapter.insertNewMessage(ChatMessageVO("6", "Как бы посмотри на ситуацию :)", "2", null, null, true, System.currentTimeMillis() + 60000L))
        adapter.insertNewMessage(ChatMessageVO("7", null, "2", arrayListOf(ImageAttachmentVO(10L).apply {
            height = 472
            width = 803
        }), null, true, System.currentTimeMillis() + 60000L))

        adapter.insertNewMessage(ChatMessageVO("8", "А, ну тогда ок )", "1", null, null, false, System.currentTimeMillis() + 60000L))
        adapter.insertNewMessage(ChatMessageVO("9", "Завтра крч буду", "2", null, null, true, System.currentTimeMillis() + 60000L))

        // New day

        adapter.insertNewMessage(ChatMessageVO("10", "Ну че, где ты?", "2", null, null, true, System.currentTimeMillis() + 86400000))
        adapter.insertNewMessage(ChatMessageVO("11", "Ща, еду", "1", null, null, false, System.currentTimeMillis() + 86400000))
        adapter.insertNewMessage(ChatMessageVO("12", null, "1", arrayListOf(ImageAttachmentVO(9L).apply {
            height = 720
            width = 1280
        }), null, false, System.currentTimeMillis() + 86400000))

        adapter.insertNewMessage(ChatMessageVO("13", "Иди пока побегай )", "1", null, null, false, System.currentTimeMillis() + 86400000))
        adapter.insertNewMessage(ChatMessageVO("13", "АХААХАХХАХА, я только что S65 порвал с места ))", "1", null, null, false, System.currentTimeMillis() + 86400000))
        adapter.insertNewMessage(ChatMessageVO("14", "Мне ещё заправиться надо, она же по 50-60 литров при разгоне хавает.", "1", null, null, false, System.currentTimeMillis() + 86400000))
        adapter.insertNewMessage(ChatMessageVO("15", "Уже два раза за день заежал на заправку", "1", null, null, false, System.currentTimeMillis() + 86400000))
        adapter.insertNewMessage(ChatMessageVO("16", "Ну зато ощущения не такие ванильные как на электротяге xD", "1", null, null, false, System.currentTimeMillis() + 86400000))

        adapter.insertNewMessage(ChatMessageVO("17", "По факту сказал )0", "2", null, null, true, System.currentTimeMillis() + 86400000))
        adapter.insertNewMessage(ChatMessageVO("18", "Давай быстрее, жду", "2", null, null, true, System.currentTimeMillis() + 86400000))
    }
}