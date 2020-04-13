package ru.sudox.android.core

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import ru.sudox.android.core.managers.NewNavigationManager
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.BACK_BUTTON_TAG
import javax.inject.Inject

private const val CORE_CONTROLLER_ROOT_VIEW_ID_KEY = "core_controller_root_view_id"

abstract class CoreController : LifecycleController() {

    private val viewModelStore = ViewModelStore()

    var appBarVO: AppBarVO? = null
    var appBarLayoutVO: AppBarLayoutVO? = null

    @Inject
    @JvmField
    var navigationManager: NewNavigationManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        (activity as CoreActivity)
                .getActivityComponent()
                .inject(this)

        return view ?: createView(container, savedViewState).apply {
            id = savedViewState
                    ?.getInt(CORE_CONTROLLER_ROOT_VIEW_ID_KEY, View.generateViewId())
                    ?: View.generateViewId()
        }
    }

    fun getViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(activity!!.application))
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        if (changeType.isEnter) {
            (activity as CoreActivity).let {
                it.setAppBarViewObject(appBarVO, ::onAppBarClicked)
                it.setAppBarLayoutViewObject(appBarLayoutVO)
            }
        }
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

    override fun onSaveViewState(view: View, outState: Bundle) {
        outState.putInt(CORE_CONTROLLER_ROOT_VIEW_ID_KEY, view.id)
    }

    override fun onDestroy() {
        viewModelStore.clear()
    }

    abstract fun createView(container: ViewGroup, savedViewState: Bundle?): View
}