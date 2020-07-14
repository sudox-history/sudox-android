package ru.sudox.android.dialogs.impl.chats

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.sudox.android.dialogs.impl.R
import ru.sudox.android.dialogs.impl.adapters.DIALOG_FOOTER_VIEW_TYPE
import ru.sudox.android.dialogs.impl.adapters.DIALOG_ITEM_VIEW_TYPE
import ru.sudox.android.dialogs.impl.viewobjects.DialogFooterViewObject
import ru.sudox.android.dialogs.impl.viewobjects.DialogViewObject
import ru.sudox.simplelists.model.BasicListItem

class ChatsViewModel @ViewModelInject constructor() : ViewModel() {

    val chatsLiveData = MutableLiveData<List<BasicListItem<*>>>()

    init {
        chatsLiveData.postValue(
            listOf(
                BasicListItem(
                    DIALOG_ITEM_VIEW_TYPE,
                    viewObject = DialogViewObject(
                        "1",
                        "Максим Митюшкин",
                        null,
                        null,
                        "Мы тут двигатель у BMW на МКАДе взорвали. Подьезжай!",
                        2,
                        isDialogMuted = false,
                        isUserOnline = true,
                        isSentByMe = false,
                        dialogTime = System.currentTimeMillis()
                    )
                ),
                BasicListItem(
                    DIALOG_ITEM_VIEW_TYPE,
                    viewObject = DialogViewObject(
                        "2",
                        "Дмитрий",
                        "11",
                        null,
                        "Пора заканчивать разработку бизнес логики и начинать тестировать проект",
                        1,
                        isDialogMuted = false,
                        isUserOnline = false,
                        isSentByMe = false,
                        dialogTime = System.currentTimeMillis()
                    )
                ),
                BasicListItem(
                    DIALOG_ITEM_VIEW_TYPE,
                    viewObject = DialogViewObject(
                        "3",
                        "undefined7887",
                        "2",
                        null,
                        "Hi dude! Where are you? I’ll m...",
                        2,
                        isDialogMuted = true,
                        isUserOnline = false,
                        isSentByMe = false,
                        dialogTime = System.currentTimeMillis()
                    )
                ),
                BasicListItem(
                    DIALOG_ITEM_VIEW_TYPE,
                    viewObject = DialogViewObject(
                        "4",
                        "Антон",
                        "1",
                        null,
                        "I am thinking you are nig...",
                        2,
                        isDialogMuted = true,
                        isUserOnline = false,
                        isSentByMe = true,
                        dialogTime = System.currentTimeMillis()
                    )
                ),
                BasicListItem(
                    DIALOG_FOOTER_VIEW_TYPE,
                    viewObject = DialogFooterViewObject(4, R.plurals.chats)
                )
            )
        )
    }
}