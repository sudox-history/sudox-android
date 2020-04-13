package ru.sudox.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.attachRouter
import ru.sudox.android.AppLoader.Companion.loaderComponent
import ru.sudox.android.core.CoreActivity
import ru.sudox.android.core.inject.CoreActivityComponent
import ru.sudox.android.core.inject.CoreActivityModule
import ru.sudox.android.core.inject.CoreLoaderComponent
import ru.sudox.android.core.managers.AUTH_ROOT_TAG
import ru.sudox.android.core.managers.DIALOGS_ROOT_TAG
import ru.sudox.android.core.managers.NewNavigationManager
import ru.sudox.android.core.managers.PEOPLE_ROOT_TAG
import ru.sudox.android.inject.components.ActivityComponent
import ru.sudox.android.layouts.AppLayout
import ru.sudox.android.managers.AppNavigationManager
import ru.sudox.android.managers.AppScreenManager
import ru.sudox.android.vos.ConnectAppBarVO
import ru.sudox.api.common.SudoxApi
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import javax.inject.Inject

/**
 * Главная Activity данного приложения.
 *
 * Отвечает за:
 * 1) Скрытие Splash-icon'а (да, он реализован с помощью windowBackground)
 * 2) Инициализацию основной View (т.е. AppLayout)
 * 3) Восстановление состояния
 * 4) Поставку Core-компонента Dagger.
 */
class AppActivity : AppCompatActivity(), CoreActivity {

    private var navigationManager: NewNavigationManager? = null
    private var activityComponent: ActivityComponent? = null
    private var routerLazy: Lazy<Router>? = null
    private var appLayout: AppLayout? = null

    @Inject
    @JvmField
    var sudoxApi: SudoxApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (window.decorView.background as LayerDrawable)
                .getDrawable(1)
                .alpha = 1

        super.onCreate(savedInstanceState)

        appLayout = AppLayout(this).apply {
            // Почему-то фрагменты не восстанавливаются если восстанавливать ID FrameLayout'а в View.onRestoreState()
            init(savedInstanceState)
        }

        // Отложим инициализацию роутера до первого его запроса (исправляем краши при инжекте из субкомпонента активности)
        routerLazy = lazy { attachRouter(appLayout!!.contentLayout.frameLayout, savedInstanceState) }
        navigationManager = AppNavigationManager(routerLazy!!, appLayout!!.bottomNavigationView).apply {
            restoreState(savedInstanceState)
        }

        setContentView(appLayout)

        if (!routerLazy!!.value.hasRootController()) {
            navigationManager!!.showRoot(DIALOGS_ROOT_TAG)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!navigationManager!!.popBackstack()) {
                super.onBackPressed()
            }

            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            navigationManager!!.saveState(this)
            appLayout!!.saveIds(this)
        })
    }

    override fun setAppBarViewObject(appBarVO: AppBarVO?, callback: ((Int) -> (Unit))?) {
        appLayout!!.contentLayout.appBarLayout.appBar!!.let {
            it.callback = callback

            if (appBarVO != null) {
                it.vo = ConnectAppBarVO(appBarVO)
            } else {
                it.vo = null
            }
        }
    }

    override fun setAppBarLayoutViewObject(appBarLayoutVO: AppBarLayoutVO?) {
        appLayout!!.contentLayout.appBarLayout.vo = appBarLayoutVO
    }

    override fun onDestroy() {
        activityComponent = null
        super.onDestroy()
    }

    override fun getActivityComponent(): CoreActivityComponent {
        if (activityComponent == null) {
            activityComponent = loaderComponent!!.activityComponent(CoreActivityModule(navigationManager!!, AppScreenManager(this)))
        }

        return activityComponent!!
    }

    override fun getLoaderComponent(): CoreLoaderComponent {
        return loaderComponent!!
    }

    override fun onBackPressed() {
        // Перенесен в onKeyDown()
    }
}