package ru.sudox.android.dialogs.impl.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.sudox.android.core.ui.lists.model.SectionVO
import ru.sudox.android.dialogs.impl.R
import ru.sudox.android.dialogs.impl.search.viewobjects.DialogsSearchResultViewObject
import ru.sudox.android.dialogs.impl.search.viewobjects.FoundChatViewObject
import ru.sudox.simplelists.model.BasicListItem

const val FOUND_CHAT_SECTION_ORDER = 0
const val FOUND_MESSAGE_SECTION_ORDER = 1

class DialogsSearchViewModel @ViewModelInject constructor() : ViewModel() {

    val resultLiveData = MutableLiveData<DialogsSearchResultViewObject>()
    val messagesSectionVO = SectionVO(titleRes = R.string.found_messages_section_title)
    val chatsSectionVO = SectionVO(titleRes = R.string.found_chats_section_title)

    init {
        resultLiveData.postValue(
            DialogsSearchResultViewObject(
                listOf(
                    BasicListItem(FOUND_CHAT_SECTION_ORDER, FoundChatViewObject("1", "Максим Митюшкин", 0L, null, true)),
                    BasicListItem(
                        FOUND_CHAT_SECTION_ORDER,
                        FoundChatViewObject("2", "Никита Казанцев", System.currentTimeMillis() - 86400000L, "3", false)
                    )
                ), listOf(

                )
            )
        )
    }
}