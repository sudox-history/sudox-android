package ru.sudox.android

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import ru.sudox.android.core.ui.navigation.popBackstack
import ru.sudox.android.main.api.MainFeatureApi
import javax.inject.Inject

/**
 * Основное Activity приложения.
 */
@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.layout_container) {

    @Inject
    lateinit var mainFeatureApi: MainFeatureApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (window.decorView.background as LayerDrawable)
            .getDrawable(1)
            .alpha = 1

        findViewById<View>(R.id.container).systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.commit {
                add(R.id.container, mainFeatureApi.getStartupFragment())
            }
        }
    }

    override fun onBackPressed() {
        if (!popBackstack(supportFragmentManager)) {
            finishAfterTransition()
        }
    }
}