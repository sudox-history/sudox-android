package com.sudox.messenger.android.friends.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.design.resizableImageButton.ResizableImageButton
import com.sudox.messenger.android.friends.R

class MaybeYouKnowItemView : ViewGroup {

    private var marginBetweenNameAndPhoto = 0
    private var marginBetweenNameAndFriendsCount = 0
    private var closeButtonRightMargin = 0
    private var closeButtonTopMargin = 0

    private var nameTextView = AppCompatTextView(context).apply { addView(this) }
    private var friendsCountTextView = AppCompatTextView(context).apply { addView(this) }
    private var photoImageView = CircleImageView(context).apply { addView(this) }
    private var closeImageButton: ResizableImageButton? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.maybeYouKnowItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MaybeYouKnowItemView, defStyleAttr, 0).use {
            closeImageButton = ResizableImageButton(ContextThemeWrapper(context,
                    it.getResourceIdOrThrow(R.styleable.MaybeYouKnowItemView_closeButtonStyle)))

            marginBetweenNameAndPhoto = it.getDimensionPixelSize(R.styleable.MaybeYouKnowItemView_marginBetweenNameAndPhoto, 0)
            marginBetweenNameAndFriendsCount = it.getDimensionPixelSize(R.styleable.MaybeYouKnowItemView_marginBetweenNameAndMutualFriendsCount, 0)
            closeButtonRightMargin = it.getDimensionPixelSize(R.styleable.MaybeYouKnowItemView_closeButtonRightMargin, 0)
            closeButtonTopMargin = it.getDimensionPixelSize(R.styleable.MaybeYouKnowItemView_closeButtonTopMargin, 0)

            photoImageView.layoutParams = LayoutParams(
                    it.getDimensionPixelSizeOrThrow(R.styleable.MaybeYouKnowItemView_photoWidth),
                    it.getDimensionPixelSizeOrThrow(R.styleable.MaybeYouKnowItemView_photoHeight))

            setTextAppearance(nameTextView, it.getResourceIdOrThrow(R.styleable.MaybeYouKnowItemView_nameTextAppearance))
            setTextAppearance(friendsCountTextView, it.getResourceIdOrThrow(R.styleable.MaybeYouKnowItemView_mutualFriendsCountTextAppearance))
            addView(closeImageButton)
        }

        nameTextView.gravity = Gravity.CENTER_HORIZONTAL
        nameTextView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        nameTextView.ellipsize = TextUtils.TruncateAt.END
        nameTextView.isSingleLine = true
        nameTextView.maxLines = 1

        friendsCountTextView.gravity = Gravity.CENTER_HORIZONTAL
        friendsCountTextView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        friendsCountTextView.ellipsize = TextUtils.TruncateAt.END
        friendsCountTextView.isSingleLine = true
        friendsCountTextView.maxLines = 1

        closeImageButton!!.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(nameTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoImageView, widthMeasureSpec, heightMeasureSpec)
        measureChild(friendsCountTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(closeImageButton, widthMeasureSpec, heightMeasureSpec)

        val textsWidthSpec = MeasureSpec.makeMeasureSpec(minimumWidth, MeasureSpec.EXACTLY)

        measureChild(nameTextView, textsWidthSpec, heightMeasureSpec)
        measureChild(friendsCountTextView, textsWidthSpec, heightMeasureSpec)

        setMeasuredDimension(minimumWidth + paddingRight + paddingLeft, minimumHeight + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val closeButtonTopBorder = closeButtonTopMargin
        val closeButtonBottomBorder = closeButtonTopBorder + closeImageButton!!.measuredHeight
        val closeButtonRightBorder = measuredWidth - closeButtonRightMargin
        val closeButtonLeftBorder = closeButtonRightBorder - closeImageButton!!.measuredWidth

        closeImageButton!!.layout(closeButtonLeftBorder, closeButtonTopBorder, closeButtonRightBorder, closeButtonBottomBorder)

        val photoTopBorder = paddingTop
        val photoBottomBorder = photoTopBorder + photoImageView.measuredHeight
        val photoLeftBorder = measuredWidth / 2 - photoImageView.measuredWidth / 2
        val photoRightBorder = photoLeftBorder + photoImageView.measuredWidth

        photoImageView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)

        val nameTopBorder = photoBottomBorder + marginBetweenNameAndPhoto
        val nameBottomBorder = nameTopBorder + nameTextView.measuredHeight
        val textsLeftBorder = paddingLeft
        val textsRightBorder = measuredWidth - paddingRight

        nameTextView.layout(textsLeftBorder, nameTopBorder, textsRightBorder, nameBottomBorder)

        val friendsCountTopBorder = nameBottomBorder + marginBetweenNameAndFriendsCount
        val friendsCountBottomBorder = friendsCountTopBorder + friendsCountTextView.measuredHeight

        friendsCountTextView.layout(textsLeftBorder, friendsCountTopBorder, textsRightBorder, friendsCountBottomBorder)
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
        photoImageView.setImageDrawable(drawable)
    }

    /**
     * Устанавливает фото пользователя
     *
     * @param bitmap Bitmap с фотографией
     */
    fun setUserPhoto(bitmap: Bitmap?) {
        photoImageView.setImageBitmap(bitmap)
    }

    /**
     * Устанавливает статус пользователя.
     */
    fun setUserOnline(isOnline: Boolean) {
        // TODO: Indicator in avatar change
    }

    /**
     * Устанавливает количество друзей
     *
     * @param friendsCount Количество друзей
     */
    fun setMutualFriendsCount(friendsCount: Int) {
        friendsCountTextView.text = context.resources.getQuantityString(
                R.plurals.friends, friendsCount, friendsCount
        )
    }
}