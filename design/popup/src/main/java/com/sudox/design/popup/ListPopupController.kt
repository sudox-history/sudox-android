package com.sudox.design.popup

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.addListener

/**
 * Контроллер Popup-окна
 */
class ListPopupController {

    var anchorView: View? = null
        private set

    var blockedView: View? = null
        private set

    var togglingAnimator: ValueAnimator? = null
        set(value) {
            field = value?.apply {
                addListener(onStart = {
                    blockedView!!.isFocusable = listPopupWindow?.isShowing == true
                }, onEnd = {
                    if (listPopupWindow?.isShowing == false) {
                        blockedView!!.isFocusable = true
                        blockedView = null
                        anchorView = null
                    } else {
                        blockedView!!.isFocusable = false
                    }
                })
            }
        }

    var listPopupWindow: ListPopupWindow? = null
        private set

    /**
     * Открывает Popup-окно, стартует аниматор
     *
     * @param popupWindow Popup-окно для открытия
     * @param anchorView Элемент-маяк
     * @param blockedView View на которой нужно заблокировать нажатия в тот момента, пока открыт Popup
     * @param gravity Позиция точки отсчета и направление вектора от нее
     * @param useAnimator Использовать аниматор?
     */
    fun show(popupWindow: ListPopupWindow,
             blockedView: View?,
             anchorView: View,
             gravity: Int,
             useAnimator: Boolean
    ) {
        if (togglingAnimator?.isRunning == true) {
            return
        }

        hide()

        this.anchorView = anchorView
        this.blockedView = blockedView

        listPopupWindow = popupWindow.apply {
            setOnDismissListener {
                if (useAnimator) {
                    togglingAnimator?.reverse()
                }
            }

            showAsDropDown(anchorView, gravity)

            if (useAnimator) {
                togglingAnimator?.start()
            }
        }
    }

    /**
     * Закрывает Popup-окно.
     */
    fun hide() {
        if (listPopupWindow != null) {
            listPopupWindow!!.dismiss()
            listPopupWindow = null
        }
    }
}