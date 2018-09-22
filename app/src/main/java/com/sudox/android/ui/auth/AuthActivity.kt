package com.sudox.android.ui.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import com.sudox.android.R
import com.sudox.android.common.auth.AUTH_ACCOUNT_MANAGER_START
import com.sudox.android.common.auth.AUTH_KEY
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.helpers.showSnackbar
import com.sudox.android.common.models.auth.state.AuthSession
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.email.AuthEmailFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import com.sudox.android.ui.common.FreezableFragment
import com.sudox.android.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authViewModel: AuthViewModel
    lateinit var authSession: AuthSession

    // Fragments
    @Inject
    lateinit var authEmailFragment: AuthEmailFragment
    @Inject
    lateinit var authConfirmFragment: AuthConfirmFragment
    @Inject
    lateinit var authRegisterFragment: AuthRegisterFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Auth key для отличия запуска через AccountManager от обычного запуска
        val authKey = intent.getIntExtra(AUTH_KEY, 1)

        // Get view model
        authViewModel = getViewModel(viewModelFactory)

        // Listen connection status ...
        authViewModel.connectionStateLiveData.observe(this, Observer {
            if (it == ConnectState.MISSING_TOKEN) {
                showMessage(getString(R.string.connection_restored))
            } else if (it == ConnectState.DISCONNECTED) {
                showMessage(getString(R.string.lost_internet_connection))
                unfreezeCurrent()
            }
        })

        // Слушаем сессию авторизации
        authViewModel.authSessionStateLiveData.observe(this, Observer {
            if (it?.status != -1) {
                // Сохраним сессию, потом пригодится :)
                authSession = it!!

                // Переключим фрагмент (новые данные он подхватит при подгрузке)
                showAuthConfirmFragment()
            }
        })

        // Слушаем сессию аккаунта (точнее статус её жизни)
        authViewModel.accountSessionLiveData.observe(this, Observer {
            if (it?.lived!!) {
                if (authKey != AUTH_ACCOUNT_MANAGER_START) {
                    showMainActivity()
                } else {
                    finish()
                }
            }
        })

        showAuthEmailFragment(null, true)
    }

    private fun unfreezeCurrent() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentAuthContainer)
                ?: return

        // Разморозим текущий фрагмент
        if (currentFragment is FreezableFragment) {
            currentFragment.unfreeze()
        }
    }

    fun showAuthEmailFragment(email: String? = null, isFirstStart: Boolean = false) {
        authEmailFragment.email = email

        // Build transaction for fragment change
        val transaction = supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentAuthContainer, authEmailFragment)

        if (isFirstStart) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        } else {
            transaction.setCustomAnimations(R.animator.fragment_slide_right_exit_anim, R.animator.fragment_slide_left_exit_anim)
        }

        transaction.commit()
    }

    fun showAuthConfirmFragment() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_anim, R.animator.fragment_slide_right_anim)
                .replace(R.id.fragmentAuthContainer, authConfirmFragment)
                .commit()
    }

    fun showAuthRegisterFragment() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_anim, R.animator.fragment_slide_right_anim)
                .replace(R.id.fragmentAuthContainer, authRegisterFragment)
                .commit()
    }

    fun showMainActivity() {

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun showMessage(message: String) = showSnackbar(this, fragmentAuthContainer, message, Snackbar.LENGTH_LONG)

    override fun onBackPressed() {
        authViewModel.closeConnection()

        // Super! (Антон, не мучай мой перфекционизм)
        super.onBackPressed()
    }
}