package com.sudox.android.ui.views

//import com.sudox.android.common.helpers.drawContactAvatar
import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import com.sudox.android.R
import com.sudox.android.common.helpers.hideKeyboard
import com.sudox.android.data.database.model.Contact
import kotlinx.android.synthetic.main.include_search_navbar_addition.view.*


@Deprecated("НАХУЙ ЭТО ГОВНО!!!!!")
class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var startListener: ((Boolean) -> Unit)? = null
    private var visible: Boolean = false
    private var animator = animate()
            .setStartDelay(0)
            .setDuration(300)

    private lateinit var foundedContact: Contact

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
        addContactHint.visibility = View.VISIBLE
        addContactHint.text = context.getString(R.string.enter_email_to_find)
        addContactCard.visibility = View.GONE
        progress_bar.visibility = View.GONE
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // Hide this view if bottom padding is negative
        if (!visible && changed) {
            translationY = -height.toFloat()

        }
    }

//    fun setSearchContact(contact: Contact) {
//        foundedContact = contact
//
//        // Change data
//        contactName.text = contact.name
//
////        if (contact.firstColor != null && contact.secondColor != null) {
////            Glide.with(this)
////                    .load(drawContactAvatar(contact))
////                    .into(contactAvatar)
////        } else {
////            TODO("if photo is not gradient")
////        }
//
//        // Change visibility of card elements
//        addContactHint.visibility = View.GONE
//        addContactCard.visibility = View.VISIBLE
//    }

    fun toggle() {
        return toggle(!visible)
    }

    fun toggle(toggle: Boolean) {
        if (toggle && !visible) {
            animator.interpolator = DecelerateInterpolator()
            animator.translationY(0F)

            visible = true
        } else if (visible) {
            animator.interpolator = AccelerateInterpolator()
            animator.translationY(-height.toFloat())
            visible = false
        }
    }
}