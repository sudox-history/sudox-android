package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R

const val FRIENDS_OPTION_TAG = 0
const val SUBSCRIPTIONS_OPTION_TAG = 1
const val SORT_CHANGING_OPTION_TAG = 2

const val POPULAR_OPTION_TAG = 7
const val FAVORITE_OPTION_TAG = 6
const val IMPORTANCE_OPTION_TAG = 3
const val ONLINE_OPTION_TAG = 4
const val NAME_OPTION_TAG = 5

class AddedFriendsHeaderVO(
        override val selectedFunctionButtonToggleIndex: Int = 0,
        override val selectedToggleIndex: Int = 0
) : ViewListHeaderVO {

    override val isItemsHidden: Boolean = false

    override fun getToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable?>>> {
        return arrayOf(
                Pair(FRIENDS_OPTION_TAG, Pair(context.getString(R.string.friends), context.getDrawable(R.drawable.ic_mood))),
                Pair(SUBSCRIPTIONS_OPTION_TAG, Pair(context.getString(R.string.subscriptions), context.getDrawable(R.drawable.ic_public)))
        )
    }

    override fun getFunctionButton(context: Context): Pair<Int, Drawable>? {
        return Pair(SORT_CHANGING_OPTION_TAG, context.getDrawable(R.drawable.ic_filter_list)!!)
    }

    override fun getFunctionButtonToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable>>>? {
        return if (selectedToggleIndex == 0) {
            arrayOf(
                    Pair(IMPORTANCE_OPTION_TAG, Pair(context.getString(R.string.importance), context.getDrawable(R.drawable.ic_show_chart)!!)),
                    Pair(ONLINE_OPTION_TAG, Pair(context.getString(R.string.online), context.getDrawable(R.drawable.ic_trip)!!)),
                    Pair(NAME_OPTION_TAG, Pair(context.getString(R.string.name), context.getDrawable(R.drawable.ic_face)!!))
            )
        } else {
            arrayOf(
                    Pair(FAVORITE_OPTION_TAG, Pair(context.getString(R.string.favorite), context.getDrawable(R.drawable.ic_favorite)!!)),
                    Pair(POPULAR_OPTION_TAG, Pair(context.getString(R.string.popular), context.getDrawable(R.drawable.ic_show_chart)!!)),
                    Pair(NAME_OPTION_TAG, Pair(context.getString(R.string.name), context.getDrawable(R.drawable.ic_face)!!))
            )
        }
    }

    override fun canHideItems(): Boolean {
        return false
    }
}