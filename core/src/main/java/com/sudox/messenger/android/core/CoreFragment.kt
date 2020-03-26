package com.sudox.messenger.android.core

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.sudox.design.appbar.vos.AppBarLayoutVO
import com.sudox.design.appbar.vos.AppBarVO
import com.sudox.design.appbar.vos.BACK_BUTTON_TAG
import com.sudox.design.hideSoftKeyboard
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.core.managers.ScreenManager
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import javax.inject.Inject

abstract class CoreFragment : Fragment(), Animator.AnimatorListener {

    private var animator: Animator? = null

    var appBarLayoutVO: AppBarLayoutVO? = null
    var appBarVO: AppBarVO? = null

    @Inject
    @JvmField
    var navigationManager: NavigationManager? = null
    @Inject
    @JvmField
    var screenManager: ScreenManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectAll(activity as CoreActivity)
        super.onCreate(savedInstanceState)
    }

    /**
     * Вызывается при клике по кнопке, находящейся в AppBar'е
     * Стандартная реализация также отрабатывает нажатие кнопки назад
     *
     * @param tag Тег кнопки, по которой был произведен клик
     */
    open fun onAppBarClicked(tag: Int) {
        if (tag == BACK_BUTTON_TAG) {
            activity!!.onKeyDown(KeyEvent.KEYCODE_BACK, null)
        }
    }

    /**
     * Подгружает все зависимости данного класса
     * Вынесено в отдельный метод для совместимости со случаями инициализации без Activity
     *
     * @param activity Основное Activity приложения
     */
    fun injectAll(activity: CoreActivity) {
        activity.getCoreComponent().inject(this)
    }

    override fun onResume() {
        if (!isHidden) {
            onHiddenChanged(false)
        }

        super.onResume()
    }

    override fun onDetach() {
        animator?.removeListener(this)
        onHiddenChanged(true)
        super.onDetach()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            // P.S.: CoreFragment можно использовать только если Activity является наследником CoreActivity
            if (this !is TabsChildFragment || !isAppBarConfiguredByRoot()) {
                (activity as CoreActivity).setAppBarViewObject(appBarVO, ::onAppBarClicked)
            }
        } else {
            activity!!.hideSoftKeyboard()
        }

        super.onHiddenChanged(hidden)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (enter && nextAnim != 0) {
            animator = AnimatorInflater.loadAnimator(context, nextAnim).apply {
                addListener(this@CoreFragment)
            }

            return animator
        }

        return super.onCreateAnimator(transit, enter, nextAnim)
    }

    override fun onAnimationCancel(animation: Animator) {
        animator?.removeListener(this)
        animator = null
    }

    override fun onAnimationEnd(animation: Animator) {
        animator?.removeListener(this)
        animator = null
    }

    override fun onAnimationRepeat(animation: Animator) {}
    override fun onAnimationStart(animation: Animator) {}
}