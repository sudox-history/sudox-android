package ru.sudox.android.auth.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.sudox.android.auth.phone.AuthPhoneViewModel
import ru.sudox.android.auth.repositories.AuthRepository
import ru.sudox.android.core.inject.viewmodel.ViewModelKey

@Module
abstract class AuthModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthPhoneViewModel::class)
    internal abstract fun authPhoneViewModel(viewModel: AuthPhoneViewModel): ViewModel

    @Binds
    internal abstract fun authRepository(authRepository: AuthRepository): Any
}