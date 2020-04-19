package ru.sudox.android.core

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.lalafo.conductor.glide.GlideProvider
import ru.sudox.android.core.managers.NewNavigationManager
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.BACK_BUTTON_TAG
import ru.sudox.design.common.hideSoftKeyboard
import javax.inject.Inject

private const val CORE_CONTROLLER_ROOT_VIEW_ID_KEY = "core_controller_root_view_id"

abstract class CoreController : LifecycleController(), GlideProvider<GlideRequests> {

    @Suppress("LeakingThis")
    private val glideControllerSupport = CoreGlideControllerSupport(this)

    var appBarVO: AppBarVO? = null
    var appBarLayoutVO: AppBarLayoutVO? = null
    val viewModelStore = ViewModelStore()

    @Inject
    @JvmField
    var navigationManager: NewNavigationManager? = null

    @Inject
    @JvmField
    var viewModelFactory: ViewModelProvider.Factory? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        (activity as CoreActivity)
                .getActivityComponent()
                .inject(this)

        if (view != null) {
            return view!!
        }

        return createView(container, savedViewState).apply {
            id = savedViewState?.getInt(CORE_CONTROLLER_ROOT_VIEW_ID_KEY, View.generateViewId()) ?: View.generateViewId()

            if (navigationManager!!.isContentUsesAllLayout()) {
                val initialBottomPadding = paddingBottom

                ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                    updatePadding(bottom = initialBottomPadding + insets.systemWindowInsetBottom)
                    insets
                }

                fitsSystemWindows = true
            }

            bindView(getViewForBind(this))
        }
    }

    override fun getGlide(): GlideRequests {
        return glideControllerSupport.glide
    }

    open fun getViewForBind(parent: View): View {
        return parent
    }

    inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(viewModelStore, viewModelFactory!!)[T::class.java]
    }

    @CallSuper
    override fun onAttach(view: View) {
        super.onAttach(view)

        if (!isChild()) {
            (activity as CoreActivity).let {
                it.setAppBarViewObject(appBarVO, ::onAppBarClicked)
                it.setAppBarLayoutViewObject(appBarLayoutVO)
            }
        }

        if (navigationManager!!.isContentUsesAllLayout()) {
            view.requestApplyInsets()
        }
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        if (!changeType.isEnter) {
            activity!!.hideSoftKeyboard()
        }
    }

    open fun onAppBarClicked(tag: Int) {
        if (tag == BACK_BUTTON_TAG) {
            // При использовании данного ядра Activity должен обрабатывать нажатие кнопки назад в методе onKeyDown()
            activity!!.onKeyDown(KeyEvent.KEYCODE_BACK, null)
        }
    }

    open fun isChild(): Boolean {
        return false
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        outState.putInt(CORE_CONTROLLER_ROOT_VIEW_ID_KEY, view.id)
    }

    @CallSuper
    override fun onDestroy() {
        viewModelStore.clear()
    }

    @CallSuper
    open fun bindView(view: View) {
    }

    abstract fun createView(container: ViewGroup, savedViewState: Bundle?): View
    abstract fun isInStartState(): Boolean
    abstract fun toStartState()
}