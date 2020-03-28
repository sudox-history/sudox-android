package com.sudox.messenger.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.sudox.design.appbar.vos.AppBarLayoutVO
import com.sudox.design.appbar.vos.AppBarVO
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreLoader
import com.sudox.messenger.android.core.inject.CoreComponent
import com.sudox.messenger.android.core.inject.CoreModule
import com.sudox.messenger.android.core.inject.DaggerCoreComponent
import com.sudox.messenger.android.layouts.AppLayout
import com.sudox.messenger.android.managers.AppNavigationManager
import com.sudox.messenger.android.managers.AppScreenManager

class AppActivity : AppCompatActivity(), CoreActivity {

    private var appLayout: AppLayout? = null
    private var coreComponent: CoreComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (window.decorView.background as LayerDrawable)
                .getDrawable(1)
                .alpha = 1

        coreComponent = DaggerCoreComponent
                .builder()
                .coreModule(CoreModule(AppNavigationManager(), AppScreenManager(this)))
                .build()

        super.onCreate(savedInstanceState)

        appLayout = AppLayout(this).apply {
            init(savedInstanceState)

            if (savedInstanceState == null) {
                contentLayout.init(supportFragmentManager)
            }
        }

        setContentView(appLayout)
    }

    override fun onBackPressed() {
        if (!findNavController(appLayout!!.contentLayout.frameLayout.id).popBackStack()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            appLayout!!.saveIds(this)
        })
    }

    override fun setAppBarViewObject(appBarVO: AppBarVO?, callback: ((Int) -> (Unit))?) {
        appLayout!!.contentLayout.appBarLayout.appBar!!.let {
            it.callback = callback
            it.vo = appBarVO
        }
    }

    override fun setAppBarLayoutViewObject(appBarLayoutVO: AppBarLayoutVO?) {
        appLayout!!
                .contentLayout
                .appBarLayout
                .vo = appBarLayoutVO
    }

    override fun getCoreComponent(): CoreComponent {
        return coreComponent!!
    }

    override fun getLoader(): CoreLoader {
        return application as CoreLoader
    }
}