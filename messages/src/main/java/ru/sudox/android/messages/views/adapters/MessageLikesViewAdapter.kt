package ru.sudox.android.messages.views.adapters

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.views.avatar.AvatarImageView
import ru.sudox.android.people.common.vos.PeopleVO

internal const val COUNT_ITEM_VIEW_TYPE = 0
internal const val AVATAR_ITEM_VIEW_TYPE = 1

class MessageLikesViewAdapter(
        private val glide: GlideRequests
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var firstLikesVOs = ArrayList<PeopleVO>(3)

    override fun getItemViewType(position: Int): Int {
        return if (firstLikesVOs.size > 3 && position == 3) {
            COUNT_ITEM_VIEW_TYPE
        } else {
            AVATAR_ITEM_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == COUNT_ITEM_VIEW_TYPE) {
            CountViewHolder(AppCompatTextView(parent.context))
        } else {
            AvatarViewHolder(AvatarImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(112, 112)
            })
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return if (firstLikesVOs.size <= 3) {
            (holder as AvatarViewHolder).view.loadImage(firstLikesVOs[firstLikesVOs.size - position - 1].photoId, glide)
        } else if (position != 3) {
            (holder as AvatarViewHolder).view.loadImage(firstLikesVOs[firstLikesVOs.size - position - 2].photoId, glide)
        } else {
            (holder as CountViewHolder).view.text = "+${firstLikesVOs.size - 3}"
        }
    }

    override fun getItemCount(): Int {
        return if (firstLikesVOs.size <= 3) {
            firstLikesVOs.size
        } else {
            4
        }
    }

    class CountViewHolder(val view: AppCompatTextView) : RecyclerView.ViewHolder(view)
    class AvatarViewHolder(val view: AvatarImageView) : RecyclerView.ViewHolder(view)
}