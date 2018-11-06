package com.sudox.android.common.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.sudox.android.common.di.annotation.ViewModelKey
import com.sudox.android.common.di.viewmodels.ViewModelFactory
import com.sudox.android.ui.auth.AuthViewModel
import com.sudox.android.ui.auth.confirm.AuthConfirmViewModel
import com.sudox.android.ui.auth.email.AuthEmailViewModel
import com.sudox.android.ui.auth.register.AuthRegisterViewModel
import com.sudox.android.ui.main.MainViewModel
import com.sudox.android.ui.main.contacts.ContactsViewModel
import com.sudox.android.ui.main.messages.MessagesViewModel
import com.sudox.android.ui.main.settings.SettingsViewModel
import com.sudox.android.ui.messages.MessagesInnerViewModel
import com.sudox.android.ui.messages.chat.ChatViewModel
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
    @ViewModelKey(ChatViewModel::class)
    internal abstract fun userChatViewModel(viewModel: ChatViewModel): ViewModel

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
    @ViewModelKey(MessagesInnerViewModel::class)
    internal abstract fun chatViewModel(viewModel: MessagesInnerViewModel): ViewModel

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
    @ViewModelKey(MessagesViewModel::class)
    internal abstract fun messagesViewModel(viewModel: MessagesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthRegisterViewModel::class)
    internal abstract fun authRegisterViewModel(viewModel: AuthRegisterViewModel): ViewModel
}