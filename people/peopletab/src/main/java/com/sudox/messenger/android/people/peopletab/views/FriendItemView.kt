package com.sudox.messenger.android.people.peopletab.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.getStringOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.imagebutton.ImageButton
import com.sudox.design.imageview.CircleImageView
import com.sudox.messenger.android.people.peopletab.R
import kotlin.math.max

class FriendItemView : ViewGroup {

    private var onlineTextColor = 0
    private var offlineTextColor = 0
    private var offlineTextMaskId = 0
    private var onlineText: String? = null

    private var marginBetweenAvatarAndTexts = 0
    private var marginBetweenNameAndStatus = 0
    private var marginBetweenButtonsAndTexts = 0
    private var marginBetweenButtons = 0

    var acceptImageButton: ImageButton? = null
    var rejectImageButton: ImageButton? = null

    private var statusTextView = AppCompatTextView(context).apply { addView(this) }
    private var photoImageView = CircleImageView(context).apply { addView(this) }
    private var nameTextView = AppCompatTextView(context).apply { addView(this) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.friendItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.FriendItemView, defStyleAttr, 0).use {
            acceptImageButton = ImageButton(ContextThemeWrapper(
                    context, it.getResourceIdOrThrow(R.styleable.FriendItemView_acceptButtonStyle)))
            rejectImageButton = ImageButton(ContextThemeWrapper(
                    context, it.getResourceIdOrThrow(R.styleable.FriendItemView_rejectButtonStyle)))

            addView(acceptImageButton)
            addView(rejectImageButton)

            setTextAppearance(nameTextView, it.getResourceIdOrThrow(R.styleable.FriendItemView_nameTextAppearance))
            setTextAppearance(statusTextView, it.getResourceIdOrThrow(R.styleable.FriendItemView_statusTextAppearance))

            val photoHeight = it.getDimensionPixelSizeOrThrow(R.styleable.FriendItemView_photoHeight)
            val photoWidth = it.getDimensionPixelSizeOrThrow(R.styleable.FriendItemView_photoWidth)

            marginBetweenAvatarAndTexts = it.getDimensionPixelSize(R.styleable.FriendItemView_marginBetweenAvatarAndTexts, 0)
            marginBetweenNameAndStatus = it.getDimensionPixelSize(R.styleable.FriendItemView_marginBetweenNameAndStatus, 0)
            marginBetweenButtonsAndTexts = it.getDimensionPixelSize(R.styleable.FriendItemView_marginBetweenButtonsAndTexts, 0)
            marginBetweenButtons = it.getDimensionPixelSize(R.styleable.FriendItemView_marginBetweenButtons, 0)

            photoImageView.layoutParams = LayoutParams(photoWidth, photoHeight)

            onlineTextColor = it.getColorOrThrow(R.styleable.FriendItemView_onlineTextColor)
            offlineTextColor = it.getColorOrThrow(R.styleable.FriendItemView_offlineTextColor)
            offlineTextMaskId = it.getResourceIdOrThrow(R.styleable.FriendItemView_offlineTextMask)
            onlineText = it.getStringOrThrow(R.styleable.FriendItemView_onlineText)
        }

        nameTextView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        nameTextView.ellipsize = TextUtils.TruncateAt.END
        nameTextView.isSingleLine = true
        nameTextView.maxLines = 1

        statusTextView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        statusTextView.ellipsize = TextUtils.TruncateAt.END
        statusTextView.isSingleLine = true
        statusTextView.maxLines = 1

        acceptImageButton!!.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        rejectImageButton!!.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        toggleAcceptAndRejectButtons(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(nameTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoImageView, widthMeasureSpec, heightMeasureSpec)
        measureChild(statusTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(rejectImageButton, widthMeasureSpec, heightMeasureSpec)
        measureChild(acceptImageButton, widthMeasureSpec, heightMeasureSpec)

        var needWidth = paddingLeft + photoImageView.measuredWidth + marginBetweenAvatarAndTexts + paddingRight

        if (acceptImageButton!!.visibility == View.VISIBLE) {
            needWidth += acceptImageButton!!.measuredWidth
            needWidth += rejectImageButton!!.measuredWidth
        }

        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textBlockWidth = availableWidth - needWidth
        val textBlockWidthSpec = MeasureSpec.makeMeasureSpec(textBlockWidth, MeasureSpec.EXACTLY)

        measureChild(nameTextView, textBlockWidthSpec, heightMeasureSpec)
        measureChild(statusTextView, textBlockWidthSpec, heightMeasureSpec)

        val needHeight = paddingTop +
                max(max(max(nameTextView.measuredHeight + statusTextView.measuredHeight, photoImageView.measuredHeight),
                        acceptImageButton!!.measuredHeight),
                        rejectImageButton!!.measuredHeight) + paddingBottom

        setMeasuredDimension(availableWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val rejectButtonRightBorder = measuredWidth - paddingRight
        val rejectButtonLeftBorder = rejectButtonRightBorder - rejectImageButton!!.measuredWidth
        val rejectButtonTopBorder = measuredHeight / 2 - rejectImageButton!!.measuredHeight / 2
        val rejectButtonBottomBorder = rejectButtonTopBorder + rejectImageButton!!.measuredHeight

        rejectImageButton!!.layout(rejectButtonLeftBorder, rejectButtonTopBorder, rejectButtonRightBorder, rejectButtonBottomBorder)

        val acceptButtonRightBorder = rejectButtonLeftBorder - marginBetweenButtons
        val acceptButtonLeftBorder = acceptButtonRightBorder - acceptImageButton!!.measuredWidth
        val acceptButtonTopBorder = measuredHeight / 2 - acceptImageButton!!.measuredHeight / 2
        val acceptButtonBottomBorder = rejectButtonTopBorder + acceptImageButton!!.measuredHeight

        acceptImageButton!!.layout(acceptButtonLeftBorder, acceptButtonTopBorder, acceptButtonRightBorder, acceptButtonBottomBorder)

        val photoLeftBorder = paddingLeft
        val photoRightBorder = photoLeftBorder + photoImageView.measuredWidth
        val photoTopBorder = paddingTop
        val photoBottomBorder = photoTopBorder + photoImageView.measuredWidth

        photoImageView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)

        val nameBottomBorder = measuredHeight / 2 + marginBetweenNameAndStatus / 2
        val nameTopBorder = nameBottomBorder - nameTextView.measuredHeight
        val nameLeftBorder = photoRightBorder + marginBetweenAvatarAndTexts
        var nameRightBorder = nameLeftBorder + nameTextView.measuredWidth

        val statusTopBorder = measuredHeight / 2 - marginBetweenNameAndStatus / 2
        val statusBottomBorder = statusTopBorder + statusTextView.measuredHeight
        val statusLeftBorder = photoRightBorder + marginBetweenAvatarAndTexts
        var statusRightBorder = statusLeftBorder + statusTextView.measuredWidth

        if (acceptImageButton!!.visibility == View.VISIBLE) {
            nameRightBorder -= marginBetweenButtons
            statusRightBorder -= marginBetweenButtons
        }

        nameTextView.layout(nameLeftBorder, nameTopBorder, nameRightBorder, nameBottomBorder)
        statusTextView.layout(statusLeftBorder, statusTopBorder, statusRightBorder, statusBottomBorder)
    }

    /**
     * Устанавливает имя пользователя
     *
     * @param name Имя пользователя
     */
    fun setUserName(name: String) {
        nameTextView.text = name
    }

    /**
     * Устанавливает фото пользователя
     *
     * @param drawable Drawable с фотографией
     */
    fun setUserPhoto(drawable: Drawable?) {
        photoImageView.setDrawable(drawable)
    }

    /**
     * Устанавливает фото пользователя
     *
     * @param bitmap Bitmap с фотографией
     */
    fun setUserPhoto(bitmap: Bitmap?) {
        photoImageView.bitmap = bitmap
    }

    /**
     * Устанавливает статус пользователя как оффлайн.
     * Форматирует и устанавливает строку с временем в статус.
     *
     * @param seenTime Последнее время онлайна
     */
    fun setUserOffline(seenTime: Long) {
        // TODO: Replace with formatTime
        statusTextView.setTextColor(offlineTextColor)
        statusTextView.text = seenTime.toString()
    }

    /**
     * Устанавливает статус пользователя как онлайн.
     */
    fun setUserOnline() {
        statusTextView.setTextColor(onlineTextColor)
        statusTextView.text = onlineText
    }

    /**
     * Включает и отключает отображение кнопок управления запросом на добавление.
     *
     * @param toggle Состояние кнопок (если true, то отображаются, если false - не отображаются)
     */
    fun toggleAcceptAndRejectButtons(toggle: Boolean) {
        if (toggle) {
            acceptImageButton!!.visibility = View.VISIBLE
            rejectImageButton!!.visibility = View.VISIBLE
        } else {
            acceptImageButton!!.visibility = View.GONE
            rejectImageButton!!.visibility = View.GONE
        }
    }
}