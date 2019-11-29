package com.sudox.messenger.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreLoader
import com.sudox.messenger.android.core.managers.ApplicationBarManager
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.core.managers.ScreenManager
import com.sudox.messenger.android.managers.AppApplicationBarManager
import com.sudox.messenger.android.managers.AppNavigationManager
import com.sudox.messenger.android.managers.AppScreenManager
import kotlinx.android.synthetic.main.activity_app.applicationBar
import kotlinx.android.synthetic.main.activity_app.navigationBar

class AppActivity : AppCompatActivity(), CoreActivity {

    private var screenManager: ScreenManager? = null
    private var navigationManager: NavigationManager? = null
    private var applicationBarManager: ApplicationBarManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (window.decorView.background as LayerDrawable)
                .getDrawable(1)
                .alpha = 1

        setContentView(R.layout.activity_app)

        screenManager = AppScreenManager(this)
        applicationBarManager = AppApplicationBarManager(this, applicationBar!!)
        navigationManager = AppNavigationManager(supportFragmentManager, navigationBar, R.id.frameContainer)

        if (savedInstanceState != null) {
            navigationManager!!.restoreState(savedInstanceState)
        } else {
            navigationManager!!.configureNavigationBar()
            navigationManager!!.showMainPart()
        }
    }

    override fun onBackPressed() {}

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

    override fun getScreenManager(): ScreenManager {
        return screenManager!!
    }

    override fun getNavigationManager(): NavigationManager {
        return navigationManager!!
    }

    override fun getApplicationBarManager(): ApplicationBarManager {
        return applicationBarManager!!
    }

    override fun getLoader(): CoreLoader {
        return application as CoreLoader
    }
}