package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.SparseIntArray
import com.sudox.design.popup.vos.PopupItemVO
import com.sudox.design.popup.vos.PopupItemWithDrawableIconVO
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.adapters.ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE

const val FRIENDS_OPTION_TAG = 0
const val SUBSCRIPTIONS_OPTION_TAG = 1

const val POPULAR_OPTION_TAG = 7
const val FAVORITE_OPTION_TAG = 6
const val IMPORTANCE_OPTION_TAG = 3
const val ONLINE_OPTION_TAG = 4
const val NAME_OPTION_TAG = 5

class AddedFriendsHeaderVO(
        override var selectedToggleTag: Int = 0,
        override var selectedFunctionButtonToggleTags: SparseIntArray? = null,
        override var isContentLoading: Boolean = false
) : ViewListHeaderVO {

    override var type: Int = ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE
    override var isItemsHidden: Boolean = false

    init {
        if (selectedFunctionButtonToggleTags == null) {
            selectedFunctionButtonToggleTags = SparseIntArray().apply {
                append(FRIENDS_OPTION_TAG, IMPORTANCE_OPTION_TAG)
                append(SUBSCRIPTIONS_OPTION_TAG, FAVORITE_OPTION_TAG)
            }
        }
    }

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(
                PopupItemWithDrawableIconVO(FRIENDS_OPTION_TAG,
                        context.getString(R.string.friends),
                        context.getDrawable(R.drawable.ic_mood)!!,
                        selectedToggleTag == FRIENDS_OPTION_TAG),
                PopupItemWithDrawableIconVO(SUBSCRIPTIONS_OPTION_TAG,
                        context.getString(R.string.subscriptions),
                        context.getDrawable(R.drawable.ic_public)!!,
                        selectedToggleTag == SUBSCRIPTIONS_OPTION_TAG)
        )
    }

    override fun getFunctionButtonIcon(context: Context): Drawable? {
        return context.getDrawable(R.drawable.ic_filter_list)!!
    }

    override fun getFunctionButtonToggleOptions(context: Context): List<PopupItemVO<*>>? {
        val functionalTag = selectedFunctionButtonToggleTags!![selectedToggleTag]

        return if (selectedToggleTag == FRIENDS_OPTION_TAG) {
            listOf(
                    PopupItemWithDrawableIconVO(IMPORTANCE_OPTION_TAG,
                            context.getString(R.string.importance),
                            context.getDrawable(R.drawable.ic_show_chart)!!,
                            functionalTag == IMPORTANCE_OPTION_TAG),
                    PopupItemWithDrawableIconVO(ONLINE_OPTION_TAG,
                            context.getString(R.string.online),
                            context.getDrawable(R.drawable.ic_trip)!!,
                            functionalTag == ONLINE_OPTION_TAG),
                    PopupItemWithDrawableIconVO(NAME_OPTION_TAG,
                            context.getString(R.string.name),
                            context.getDrawable(R.drawable.ic_face)!!,
                            functionalTag == NAME_OPTION_TAG)
            )
        } else {
            listOf(
                    PopupItemWithDrawableIconVO(FAVORITE_OPTION_TAG,
                            context.getString(R.string.favorite),
                            context.getDrawable(R.drawable.ic_favorite)!!,
                            functionalTag == FAVORITE_OPTION_TAG),
                    PopupItemWithDrawableIconVO(POPULAR_OPTION_TAG,
                            context.getString(R.string.popular),
                            context.getDrawable(R.drawable.ic_show_chart)!!,
                            functionalTag == POPULAR_OPTION_TAG),
                    PopupItemWithDrawableIconVO(NAME_OPTION_TAG,
                            context.getString(R.string.name),
                            context.getDrawable(R.drawable.ic_face)!!,
                            functionalTag == NAME_OPTION_TAG)
            )
        }
    }

    override fun canHideItems(): Boolean {
        return false
    }

    override fun canSortItems(): Boolean {
        return true
    }
}