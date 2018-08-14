package com.sudox.android.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.main.ContactsFragment
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = getViewModel(viewModelFactory)

        // init listeners
        mainViewModel.initContactsListeners()
        mainViewModel.loadContacts()

        supportFragmentManager.
            beginTransaction()
                    .replace(R.id.fragment_main_container, ContactsFragment())
                    .commit()
    }

    override fun onBackPressed() {
        mainViewModel.disconnect()
        super.onBackPressed()
    }
}