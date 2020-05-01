package ru.sudox.android.auth.ui.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.sudox.android.auth.ui.code.AuthCodeViewModel
import ru.sudox.android.auth.ui.phone.AuthPhoneViewModel
import ru.sudox.android.auth.ui.signup.AuthSignUpViewModel
import ru.sudox.android.auth.ui.verify.AuthVerifyViewModel
import ru.sudox.android.core.inject.viewmodel.ViewModelKey

@Module
abstract class AuthUiModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthPhoneViewModel::class)
    internal abstract fun authPhoneViewModel(viewModel: AuthPhoneViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthCodeViewModel::class)
    internal abstract fun authCodeViewModel(viewModel: AuthCodeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthSignUpViewModel::class)
    internal abstract fun authSignUpViewModel(viewModel: AuthSignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthVerifyViewModel::class)
    internal abstract fun authVerifyViewModel(viewModel: AuthVerifyViewModel): ViewModel
}