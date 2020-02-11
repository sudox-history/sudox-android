package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.popup.vos.PopupItemVO
import com.sudox.design.popup.vos.PopupItemWithDrawableIconVO
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R

const val FRIENDS_OPTION_TAG = 0
const val SUBSCRIPTIONS_OPTION_TAG = 1

const val POPULAR_OPTION_TAG = 7
const val FAVORITE_OPTION_TAG = 6
const val IMPORTANCE_OPTION_TAG = 3
const val ONLINE_OPTION_TAG = 4
const val NAME_OPTION_TAG = 5

class AddedFriendsHeaderVO(
        override var selectedFunctionButtonToggleIndex: Int = 0,
        override var selectedToggleIndex: Int = 0
) : ViewListHeaderVO {

    override var isItemsHidden: Boolean = false

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(
                PopupItemWithDrawableIconVO(FRIENDS_OPTION_TAG,
                        context.getString(R.string.friends),
                        context.getDrawable(R.drawable.ic_mood)!!,
                        selectedToggleIndex == 0),
                PopupItemWithDrawableIconVO(SUBSCRIPTIONS_OPTION_TAG,
                        context.getString(R.string.subscriptions),
                        context.getDrawable(R.drawable.ic_public)!!,
                        selectedToggleIndex == 1)
        )
    }

    override fun getFunctionButtonIcon(context: Context): Drawable? {
        return context.getDrawable(R.drawable.ic_filter_list)!!
    }

    override fun getFunctionButtonToggleOptions(context: Context): List<PopupItemVO<*>>? {
        return if (selectedToggleIndex == 0) {
            listOf(
                    PopupItemWithDrawableIconVO(IMPORTANCE_OPTION_TAG,
                            context.getString(R.string.importance),
                            context.getDrawable(R.drawable.ic_show_chart)!!,
                            selectedToggleIndex == 0),
                    PopupItemWithDrawableIconVO(ONLINE_OPTION_TAG,
                            context.getString(R.string.online),
                            context.getDrawable(R.drawable.ic_trip)!!,
                            selectedToggleIndex == 1),
                    PopupItemWithDrawableIconVO(NAME_OPTION_TAG,
                            context.getString(R.string.name),
                            context.getDrawable(R.drawable.ic_face)!!,
                            selectedToggleIndex == 2)
            )
        } else {
            listOf(
                    PopupItemWithDrawableIconVO(FAVORITE_OPTION_TAG,
                            context.getString(R.string.favorite),
                            context.getDrawable(R.drawable.ic_favorite)!!,
                            selectedToggleIndex == 0),
                    PopupItemWithDrawableIconVO(POPULAR_OPTION_TAG,
                            context.getString(R.string.popular),
                            context.getDrawable(R.drawable.ic_show_chart)!!,
                            selectedToggleIndex == 1),
                    PopupItemWithDrawableIconVO(NAME_OPTION_TAG,
                            context.getString(R.string.name),
                            context.getDrawable(R.drawable.ic_face)!!,
                            selectedToggleIndex == 2)
            )
        }
    }

    override fun canHideItems(): Boolean {
        return false
    }
}