package com.sudox.messenger.android.people.friends

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.messenger.android.people.R
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.max
import kotlin.math.min

class FriendRequestsItemView : ViewGroup {

    private val avatar = CircleImageView(context).apply { addView(this) }
    private val name = AppCompatTextView(context).apply { addView(this) }
    private val onlineStatus = TextView(context).apply { addView(this) }
    private val acceptButton = AppCompatImageButton(context).apply { addView(this) }
    private val rejectButton = AppCompatImageButton(context).apply { addView(this) }

    private var nameTextMarginRelativePhoto = 0
    private var acceptButtonMarginRelativeReject = 0
    private var acceptButtonSide = 0
    private var rejectButtonSide = 0
    private var avatarRadius = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.friendRequestsItemStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.FriendRequestsItemView, defStyleAttr, 0).use {
            avatarRadius = it.getDimensionPixelSize(R.styleable.FriendRequestsItemView_avatarRadius, 0)

            TextViewCompat.setTextAppearance(name, it.getResourceIdOrThrow(R.styleable.FriendRequestsItemView_nameTextAppearance))
            nameTextMarginRelativePhoto = it.getDimensionPixelSize(R.styleable.FriendRequestsItemView_nameTextMarginRelativePhoto, 0)

            TextViewCompat.setTextAppearance(onlineStatus, it.getResourceIdOrThrow(R.styleable.FriendRequestsItemView_onlineStatusTextAppearance))

            acceptButtonMarginRelativeReject = it.getDimensionPixelSize(R.styleable.FriendRequestsItemView_acceptButtonMarginRelativeReject, 0)
            acceptButton.setImageDrawable(it.getDrawableOrThrow(R.styleable.FriendRequestsItemView_acceptButtonDrawable))
            acceptButtonSide = it.getDimensionPixelSize(R.styleable.FriendRequestsItemView_acceptButtonSide, 0)

            rejectButton.setImageDrawable(it.getDrawableOrThrow(R.styleable.FriendRequestsItemView_rejectButtonDrawable))
            rejectButtonSide = it.getDimensionPixelSize(R.styleable.FriendRequestsItemView_rejectButtonSide, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        val needHeight = paddingTop + paddingBottom +
                max(listOf(2 * avatarRadius,
                acceptButtonSide,
                rejectButtonSide,
                (name.textSize + onlineStatus.textSize).toInt()))

        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        avatar.measure(MeasureSpec.makeMeasureSpec(avatarRadius * 2, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(avatarRadius * 2, MeasureSpec.EXACTLY))
        acceptButton.measure(MeasureSpec.makeMeasureSpec(acceptButtonSide, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(acceptButtonSide, MeasureSpec.EXACTLY))
        rejectButton.measure(MeasureSpec.makeMeasureSpec(rejectButtonSide, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(rejectButtonSide, MeasureSpec.EXACTLY))

        name.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(measuredHeight / 2, MeasureSpec.AT_MOST))
        onlineStatus.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(measuredHeight / 2, MeasureSpec.AT_MOST))

        val needWidth = paddingLeft +
                avatar.measuredWidth + nameTextMarginRelativePhoto +
                max(name.measuredWidth, onlineStatus.measuredWidth) +
                acceptButton.measuredWidth + acceptButtonMarginRelativeReject +
                rejectButton.measuredWidth + paddingRight

        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            availableWidth
        } else {
            needWidth
        }

        val leftWidth = measuredWidth -
                paddingLeft - avatar.measuredWidth -
                nameTextMarginRelativePhoto -
                acceptButton.measuredWidth - acceptButtonMarginRelativeReject -
                rejectButton.measuredWidth - paddingRight

        name.measure(MeasureSpec.makeMeasureSpec(leftWidth,MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(measuredHeight / 2, MeasureSpec.AT_MOST))
        onlineStatus.measure(MeasureSpec.makeMeasureSpec(leftWidth,MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(measuredHeight / 2, MeasureSpec.AT_MOST))

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var curPos = paddingLeft
        avatar.layout(curPos, paddingTop, curPos + avatar.measuredWidth, paddingTop + avatar.measuredHeight)

        val center = measuredHeight / 2

        curPos += avatar.measuredWidth + nameTextMarginRelativePhoto
        name.layout(curPos, paddingTop + center - name.measuredHeight, curPos + name.measuredWidth, paddingTop + center)
        onlineStatus.layout(curPos, paddingTop + center, curPos + onlineStatus.measuredWidth,
                paddingTop + center + onlineStatus.measuredHeight)

        curPos = r - l - paddingRight
        rejectButton.layout(curPos - rejectButton.measuredWidth, paddingTop + center - rejectButton.measuredHeight / 2,
                curPos, paddingTop + center - rejectButton.measuredHeight / 2 + rejectButton.measuredHeight)

        curPos -= rejectButton.measuredWidth + acceptButtonMarginRelativeReject
        acceptButton.layout(curPos - acceptButton.measuredWidth, paddingTop + center - acceptButton.measuredHeight / 2,
                curPos, paddingTop + center - acceptButton.measuredHeight / 2 + acceptButton.measuredHeight)
    }

    fun setNameText(name: String) {
        this.name.text = name
    }

    fun setAvatar(drawable: Drawable?) {
        avatar.setImageDrawable(drawable)
    }

    fun setAvatar(bitmap: Bitmap?) {
        avatar.setImageBitmap(bitmap)
    }

    fun setOnlineStatus(status: Boolean) {
        onlineStatus.text = if(status)
            "Online"
        else
            "Offline"
    }
}