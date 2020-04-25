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
import ru.sudox.android.core.managers.AppBarManager
import ru.sudox.android.core.managers.NavigationManager
import ru.sudox.android.core.managers.SearchManager
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.APPBAR_BACK_BUTTON_TAG
import ru.sudox.design.appbar.vos.APPBAR_SEARCH_BUTTON_TAG
import ru.sudox.design.appbar.vos.others.APPBAR_SEARCH_CANCEL_BUTTON_TAG
import ru.sudox.design.common.hideSoftKeyboard
import javax.inject.Inject

private const val CORE_CONTROLLER_ROOT_VIEW_ID_KEY = "core_controller_root_view_id"

abstract class CoreController : LifecycleController(), GlideProvider<GlideRequests> {

    @Suppress("LeakingThis")
    private val glideControllerSupport = CoreGlideControllerSupport(this)
    private var isSearchEnabledBeforeStop = false

    var appBarVO: AppBarVO? = null
        set(value) {
            if (!isChild() && isAttached) {
                appBarManager!!.setVO(value, ::onAppBarClicked)
            }

            field = value
        }

    var appBarLayoutVO: AppBarLayoutVO? = null
        set(value) {
            if (!isChild() && isAttached) {
                appBarManager!!.setLayoutVO(value)
            }

            field = value
        }

    val viewModelStore = ViewModelStore()

    @Inject
    @JvmField
    var searchManager: SearchManager? = null

    @Inject
    @JvmField
    var navigationManager: NavigationManager? = null

    @Inject
    @JvmField
    var appBarManager: AppBarManager? = null

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

        searchManager!!.restoreSearchState(savedViewState, callback = ::onAppBarClicked, searchCallback = ::onSearchRequest)

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
            appBarManager!!.setVO(appBarVO, ::onAppBarClicked)
            appBarManager!!.setLayoutVO(appBarLayoutVO)
        }

        appBarManager!!.requestElevationToggling(toggle = false, animate = false)

        if (navigationManager!!.isContentUsesAllLayout()) {
            view.requestApplyInsets()
        }
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        if (!changeType.isEnter) {
            activity!!.hideSoftKeyboard()
        }
    }

    open fun onSearchRequest(text: String?) {
    }

    open fun onAppBarClicked(tag: Int) {
        if (tag == APPBAR_BACK_BUTTON_TAG) {
            if (searchManager!!.isSearchEnabled()) {
                searchManager!!.toggleSearch(false)
            } else {
                // При использовании данного ядра Activity должен обрабатывать нажатие кнопки назад в методе onKeyDown()
                activity!!.onKeyDown(KeyEvent.KEYCODE_BACK, null)
            }
        } else if (tag == APPBAR_SEARCH_BUTTON_TAG) {
            searchManager!!.toggleSearch(true, callback = ::onAppBarClicked, searchCallback = ::onSearchRequest)
        } else if (tag == APPBAR_SEARCH_CANCEL_BUTTON_TAG) {
            searchManager!!.resetSearch()
        }
    }

    open fun isChild(): Boolean {
        return false
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        outState.putInt(CORE_CONTROLLER_ROOT_VIEW_ID_KEY, view.id)
        searchManager!!.saveSearchState(outState)
    }

    @CallSuper
    override fun onDestroy() {
        viewModelStore.clear()

        if (searchManager?.isSearchEnabled() == true) {
            searchManager?.toggleSearch(false)
        }
    }

    @CallSuper
    open fun bindView(view: View) {
    }

    open fun toStartState() {
    }

    open fun isInStartState(): Boolean {
        return true
    }

    abstract fun createView(container: ViewGroup, savedViewState: Bundle?): View
}