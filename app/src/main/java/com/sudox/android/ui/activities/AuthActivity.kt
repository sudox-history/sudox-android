package com.sudox.android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.sudox.android.R
import com.sudox.android.ui.fragments.AuthFragment

class AuthActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        with(supportFragmentManager.beginTransaction()) {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(R.id.fragment_auth_container, AuthFragment())
            commitAllowingStateLoss()
        }
    }
}