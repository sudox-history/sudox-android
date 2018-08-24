package com.sudox.android.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.common.viewmodels.ViewModelFactory
import com.sudox.android.di.annotation.ViewModelKey
import com.sudox.android.ui.MainViewModel
import com.sudox.android.ui.auth.AuthViewModel
import com.sudox.android.ui.auth.confirm.AuthConfirmViewModel
import com.sudox.android.ui.auth.email.AuthEmailViewModel
import com.sudox.android.ui.auth.register.AuthRegisterViewModel
import com.sudox.android.ui.main.chats.ChatViewModel
import com.sudox.android.ui.main.contacts.ContactsViewModel
import com.sudox.android.ui.main.settings.SettingsViewModel
import com.sudox.android.ui.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun splashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    internal abstract fun authViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthEmailViewModel::class)
    internal abstract fun authEmailViewModel(viewModel: AuthEmailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthConfirmViewModel::class)
    internal abstract fun authConfirmViewModel(viewModel: AuthConfirmViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactsViewModel::class)
    internal abstract fun contactsViewModel(viewModel: ContactsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    internal abstract fun chatViewModel(viewModel: ChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthRegisterViewModel::class)
    internal abstract fun authRegisterViewModel(viewModel: AuthRegisterViewModel): ViewModel


}