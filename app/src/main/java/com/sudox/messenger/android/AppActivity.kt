package com.sudox.messenger.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.sudox.design.appbar.AppBarVO
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreLoader
import com.sudox.messenger.android.core.inject.CoreComponent
import com.sudox.messenger.android.core.inject.CoreModule
import com.sudox.messenger.android.core.inject.DaggerCoreComponent
import com.sudox.messenger.android.layouts.AppLayout
import com.sudox.messenger.android.managers.AppNavigationManager
import com.sudox.messenger.android.managers.AppScreenManager

class AppActivity : AppCompatActivity(), CoreActivity {

    private var navigationManager: AppNavigationManager? = null
    private var coreComponent: CoreComponent? = null
    private var appLayout: AppLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (window.decorView.background as LayerDrawable)
                .getDrawable(1)
                .alpha = 1

        appLayout = AppLayout(this).apply {
            init(savedInstanceState)

            navigationManager = AppNavigationManager(
                    supportFragmentManager,
                    contentLayout.layoutChild.frameLayout.id,
                    navigationBar
            )

            setContentView(this)
        }

        coreComponent = DaggerCoreComponent
                .builder()
                .coreModule(CoreModule(navigationManager!!, AppScreenManager(this)))
                .build()

        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            navigationManager!!.restoreState(savedInstanceState)
        } else {
            navigationManager!!.configureNavigationBar()
            navigationManager!!.showMainPart()
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
        super.onSaveInstanceState(outState)

        appLayout!!.saveIds(outState)
        navigationManager!!.saveState(outState)
    }

    override fun setAppBarViewObject(vo: AppBarVO?) {
        appLayout!!.contentLayout.layoutChild.appBarLayout.appBar!!.vo = vo
    }

    override fun getCoreComponent(): CoreComponent {
        return coreComponent!!
    }

    override fun getLoader(): CoreLoader {
        return application as CoreLoader
    }

    override fun onBackPressed() {
    }
}