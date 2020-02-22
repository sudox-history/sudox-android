package com.sudox.messenger.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreLoader
import com.sudox.messenger.android.core.inject.CoreComponent
import com.sudox.messenger.android.core.inject.CoreModule
import com.sudox.messenger.android.core.inject.DaggerCoreComponent
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.managers.AppApplicationBarManager
import com.sudox.messenger.android.managers.AppNavigationManager
import com.sudox.messenger.android.managers.AppScreenManager
import kotlinx.android.synthetic.main.activity_app.applicationBar
import kotlinx.android.synthetic.main.activity_app.navigationBar

class AppActivity : AppCompatActivity(), CoreActivity {

    private var navigationManager: NavigationManager? = null
    private var coreComponent: CoreComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        (window.decorView.background as LayerDrawable)
                .getDrawable(1)
                .alpha = 1

        setContentView(R.layout.activity_app)

        val screenManager = AppScreenManager(this)
        val applicationBarManager = AppApplicationBarManager(this, applicationBar!!)

        navigationManager = AppNavigationManager(supportFragmentManager, navigationBar, R.id.frameContainer)
        coreComponent = DaggerCoreComponent
                .builder()
                .coreModule(CoreModule(applicationBarManager, navigationManager!!, screenManager))
                .build()

        if (savedInstanceState != null) {
            navigationManager!!.restoreState(savedInstanceState)
        } else {
            navigationManager!!.configureNavigationBar()
            navigationManager!!.showAuthPart()
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
        navigationManager!!.saveState(outState)
        super.onSaveInstanceState(outState)
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