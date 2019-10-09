package com.sudox.messenger.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.managers.AppNavigationManager

class AppActivity : AppCompatActivity(), CoreActivity {

    private val navigationManager = AppNavigationManager(supportFragmentManager, R.id.frameContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        if (savedInstanceState != null) {
            navigationManager.restoreState(savedInstanceState)
        } else {
            TODO("Start fragment")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationManager.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun getNavigationManager(): NavigationManager {
        return navigationManager
    }
}