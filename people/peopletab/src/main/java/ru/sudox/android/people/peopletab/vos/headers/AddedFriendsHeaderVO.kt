package ru.sudox.android.people.peopletab.vos.headers

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.popup.vos.PopupItemVO
import ru.sudox.design.popup.vos.PopupItemWithDrawableIconVO
import ru.sudox.design.viewlist.vos.ViewListHeaderVO
import ru.sudox.android.people.peopletab.R
import ru.sudox.android.people.peopletab.adapters.ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE

const val FRIENDS_OPTION_TAG = 0
const val SUBSCRIPTIONS_OPTION_TAG = 1

const val POPULAR_OPTION_TAG = 7
const val FAVORITE_OPTION_TAG = 6
const val IMPORTANCE_OPTION_TAG = 3
const val ONLINE_OPTION_TAG = 4
const val NAME_OPTION_TAG = 5

/**
 * ViewObject для шапки секции добавленных друзей или подписок.
 */
class AddedFriendsHeaderVO : ViewListHeaderVO {

    override var type = ADDED_FRIENDS_AND_SUBSCRIPTIONS_HEADER_TYPE

    constructor(source: Parcel) : super(source)
    constructor() : super() {
        selectedFunctionButtonToggleTags = intArrayOf(IMPORTANCE_OPTION_TAG, FAVORITE_OPTION_TAG)
        selectedToggleTag = FRIENDS_OPTION_TAG
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

    override fun getFunctionButtonIconId(): Int {
        return R.drawable.ic_filter_list
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

    companion object CREATOR : Parcelable.Creator<ViewListHeaderVO> {
        override fun createFromParcel(source: Parcel): ViewListHeaderVO {
            return AddedFriendsHeaderVO(source)
        }

        override fun newArray(size: Int): Array<ViewListHeaderVO?> {
            return arrayOfNulls(size)
        }
    }
}