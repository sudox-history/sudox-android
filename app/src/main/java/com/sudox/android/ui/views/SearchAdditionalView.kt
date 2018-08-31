package com.sudox.android.ui.views

import android.animation.Animator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.common.helpers.hideKeyboard
import com.sudox.android.database.model.Contact
import kotlinx.android.synthetic.main.card_add_contact.view.*
import kotlinx.android.synthetic.main.include_search_navbar_addition.view.*

class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var startListener: ((Boolean) -> Unit)? = null
    private var visible: Boolean = false
    private var animator = animate()
            .setStartDelay(0)
            .setDuration(300)

    private lateinit var contactP: Contact

    init {
        inflate(context, R.layout.include_search_navbar_addition, this)

        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                clearText()
                clearSearchContact()
            }

            override fun onAnimationStart(animation: Animator?) {
                startListener?.invoke(visible)
            }
        })
    }

    private fun clearText() {
        nicknameEditText.setText("")

        // Remove focus & hide keyboard
        focusedChild?.clearFocus()
        hideKeyboard(context, this@SearchAdditionalView)
    }

    private fun clearSearchContact() {
        add_contact_hint.visibility = View.VISIBLE
        add_contact_hint.text = context.getString(R.string.enter_email_to_find)
        card_add_contact.visibility = View.GONE
        progress_bar.visibility = View.GONE
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // Hide this view if bottom padding is negative
        if (!visible && changed) {
            translationY = -height.toFloat()

        }
    }

    fun setSearchContact(contact: Contact?) {
        if (contact != null) {
            contactP = contact
            add_contact_hint.visibility = View.GONE
            card_add_contact.visibility = View.VISIBLE
            if (contact.firstColor != null && contact.secondColor != null) {
                val builder = StringBuilder()
                val names = contact.name.split(" ")

                if (names.isNotEmpty()) {
                    builder.append(names[0][0])
                }

                if (names.size >= 2) {
                    builder.append(names[1][0])
                }

                // Build text
                val text = builder.toString()

                // Get bitmap
                val gradientBitmap = drawGradientBitmap(contact.firstColor!!, contact.secondColor!!, text)

                // Load image
                Glide.with(this).load(gradientBitmap).into(avatar_search)
            } else {
                TODO("if photo is not gradient")
            }

            name_search.text = contact.name
        }
    }

    private fun drawGradientBitmap(firstColor: String, secondColor: String, text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Enable antialiasing
        paint.isAntiAlias = true

        // Draw gradient
        paint.shader = LinearGradient(100F, 0F, 100F, 200F,
                Color.parseColor(firstColor), Color.parseColor(secondColor), Shader.TileMode.REPEAT)

        // Draw circle
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), 180F, paint)

        // Text bounds
        val textRect = Rect()

        // Draw text
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = 60F
        paint.getTextBounds(text, 0, text.length, textRect)
        canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

        return bitmap
    }

    fun toggle() {
        return toggle(!visible)
    }

    fun toggle(toggle: Boolean) {
       if (toggle && !visible) {
            animator.interpolator = DecelerateInterpolator()
            animator.translationY(0F)

            visible = true
        } else if (visible){
            animator.interpolator = AccelerateInterpolator()
            animator.translationY(-height.toFloat())
            visible = false
        }
    }
}