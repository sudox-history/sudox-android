package ru.sudox.android.people.impl.activity

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.sudox.android.core.ui.lists.model.SectionVO
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.activity.viewobject.MyStoryViewObject
import ru.sudox.android.people.impl.activity.viewobject.OtherStoryViewObject
import ru.sudox.android.people.impl.activity.viewobject.PostViewObject
import ru.sudox.simplelists.model.BasicListItem

const val STORIES_SECTION_ORDER = 0
const val NEWS_SECTION_ORDER = 1
const val MY_STORY_SECTION_ORDER = 0
const val OTHER_STORY_SECTION_ORDER = 1

class ActivityViewModel @ViewModelInject constructor() : ViewModel() {

    val newsSectionVO = SectionVO(titleRes = R.string.news_section_title)
    val myStoryLiveData = MutableLiveData<BasicListItem<MyStoryViewObject>>()
    val storiesLiveData = MutableLiveData<List<BasicListItem<OtherStoryViewObject>>>()
    val postsLiveData = MutableLiveData<List<BasicListItem<PostViewObject>>>()

    init {
        myStoryLiveData.postValue(BasicListItem(MY_STORY_SECTION_ORDER, MyStoryViewObject("1", "Максим Митюшкин", "4", false)))
        storiesLiveData.postValue(
            listOf(
                BasicListItem(OTHER_STORY_SECTION_ORDER, OtherStoryViewObject("1", "Дмитрий Парамонов", "11", false, false)),
                BasicListItem(OTHER_STORY_SECTION_ORDER, OtherStoryViewObject("1", "Антон Янкин", "1", false, false)),
                BasicListItem(OTHER_STORY_SECTION_ORDER, OtherStoryViewObject("1", "Никита Казанцев", "3", false, true)),
                BasicListItem(OTHER_STORY_SECTION_ORDER, OtherStoryViewObject("1", "Ярослав Евстафьев", "2", true, false)),
                BasicListItem(OTHER_STORY_SECTION_ORDER, OtherStoryViewObject("1", "Андрей Кирюхин", "5", true, false))
            )
        )

        postsLiveData.postValue(
            listOf(
                BasicListItem(
                    NEWS_SECTION_ORDER,
                    PostViewObject(
                        "1",
                        "Overwatch 2 is coming! Are you waiting for it?",
                        "1",
                        "isp",
                        "3",
                        System.currentTimeMillis() - 120000
                    )
                )
            )
        )
    }
}