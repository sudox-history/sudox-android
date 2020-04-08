package ru.sudox.android.core

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.BACK_BUTTON_TAG
import ru.sudox.design.common.hideSoftKeyboard
import ru.sudox.android.core.managers.NavigationManager
import ru.sudox.android.core.managers.ScreenManager
import ru.sudox.android.core.tabs.TabsChildFragment
import javax.inject.Inject

/**
 * Фрагмент, содержащий исправления, необходимые для корректной работы приложения.
 *
 * Улучшено и исправлено:
 * 1) onHiddenChanged() теперь также вызывается при первом появлении и скрытии фрагмента
 * 2) Удобная простановка ViewObject'ов для AppBarLayout и AppBar. Больше не нужно пилить костыль для доступа к Activity.
 * 3) Подгрузка основных компонентов приложения (NavigationManager и ScreenManager).
 * 4) Связь с AppBar из Sudox Design Library, а именно реализована обработка кнопки назад.
 * 5) Перехват анимаций для получения возможности загрузки тяжелого контента после неё.
 */
abstract class CoreFragment : Fragment(), Animator.AnimatorListener {

    var animator: Animator? = null
    var coreActivity: CoreActivity? = null
    var appBarLayoutVO: AppBarLayoutVO? = null
    var appBarVO: AppBarVO? = null

    @Inject
    @JvmField
    var screenManager: ScreenManager? = null
    @Inject
    @JvmField
    var navigationManager: NavigationManager? = null
    @Inject
    @JvmField
    var viewModelFactory: ViewModelProvider.Factory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        coreActivity = activity as CoreActivity
        coreActivity!!.getActivityComponent().inject(this)

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
            // При использовании данного ядра Activity должен обрабатывать нажатие кнопки назад в методе onKeyDown()
            activity!!.onKeyDown(KeyEvent.KEYCODE_BACK, null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (!isHidden) {
            // Исправляем гугловскую недоработку с вызовом onHiddenChanged
            // Почему-то он вызывается только если фрагмент был скрыт или отображен повторный раз
            onHiddenChanged(false)
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onPause() {
        animator?.removeListener(this)

        // Исправляем гугловскую недоработку с вызовом onHiddenChanged
        // Почему-то он вызывается только если фрагмент был скрыт или отображен повторный раз
        onHiddenChanged(true)
        super.onPause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            // P.S.: TabChildFragment'ы могут иметь свой AppBarVO, но если это разрешено в TabRootFragment
            if (this !is TabsChildFragment || !isAppBarConfiguredByRoot()) {
                coreActivity!!.setAppBarViewObject(appBarVO, ::onAppBarClicked)
            }

            // Также TabChildFragment'ы не могут иметь свой AppBarLayoutVO
            if (this !is TabsChildFragment) {
                coreActivity!!.setAppBarLayoutViewObject(appBarLayoutVO)
            }
        } else {
            activity!!.hideSoftKeyboard()
        }

        super.onHiddenChanged(hidden)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (enter && nextAnim != 0) {
            // Выполняем перехват анимации, ибо возможно нам пригодится подгружать данные только после окончании анимации.
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

    /**
     * Находится ли фрагмент в стартовом состоянии.
     * Используется для сброса состояния TabsRootFragment.
     */
    open fun isInStartState(): Boolean {
        return true
    }

    /**
     * Сбрасывает состояние фрагмента (отскроливает список обратно наверх или переключает обратно на первый таб)
     */
    open fun resetFragment() {
    }

    override fun onAnimationRepeat(animation: Animator) {}
    override fun onAnimationStart(animation: Animator) {}
}