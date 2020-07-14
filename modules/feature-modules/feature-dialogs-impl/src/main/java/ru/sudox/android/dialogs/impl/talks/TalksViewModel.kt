package ru.sudox.android.dialogs.impl.talks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.sudox.android.dialogs.impl.R
import ru.sudox.android.dialogs.impl.adapters.DIALOG_FOOTER_VIEW_TYPE
import ru.sudox.android.dialogs.impl.adapters.DIALOG_ITEM_VIEW_TYPE
import ru.sudox.android.dialogs.impl.viewobjects.DialogFooterViewObject
import ru.sudox.android.dialogs.impl.viewobjects.DialogViewObject
import ru.sudox.simplelists.model.BasicListItem

class TalksViewModel @ViewModelInject constructor() : ViewModel() {

    val talksLiveData = MutableLiveData<List<BasicListItem<*>>>()

    init {
        talksLiveData.postValue(
            listOf(
                BasicListItem(
                    DIALOG_ITEM_VIEW_TYPE,
                    DialogViewObject(
                        "1",
                        "Jeremy Clarkson fan club",
                        "6",
                        "Jeremy",
                        "Today I will be on air at 18:00.",
                        1524,
                        isDialogMuted = false,
                        isUserOnline = false,
                        isSentByMe = false,
                        dialogTime = System.currentTimeMillis()
                    )
                ),
                BasicListItem(
                    DIALOG_ITEM_VIEW_TYPE,
                    DialogViewObject(
                        "1",
                        "Sudox News",
                        "12",
                        null,
                        "Hi dude! Where are you",
                        102,
                        isDialogMuted = true,
                        isUserOnline = false,
                        isSentByMe = true,
                        dialogTime = System.currentTimeMillis() - 60000L
                    )
                ),
                BasicListItem(
                    DIALOG_ITEM_VIEW_TYPE,
                    DialogViewObject(
                        "1",
                        "Inglurious Bastards",
                        null,
                        "Яков",
                        "Время шпотьхаты 3.0",
                        0,
                        isDialogMuted = true,
                        isUserOnline = false,
                        isSentByMe = true,
                        dialogTime = System.currentTimeMillis() - 60 * 60 * 24 * 1000
                    )
                ),
                BasicListItem(DIALOG_FOOTER_VIEW_TYPE, DialogFooterViewObject(3, R.plurals.talks))
            )
        )
    }
}