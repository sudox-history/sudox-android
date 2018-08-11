package com.sudox.android.ui

import android.os.Bundle
import com.sudox.android.R
import com.sudox.android.ui.main.ContactsFragment
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.apply {
            beginTransaction()
                    .replace(R.id.fragment_main_container, ContactsFragment())
                    .commit()
        }
    }
}