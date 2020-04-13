package ru.sudox.android.auth.data.inject

import dagger.Binds
import dagger.Module
import ru.sudox.android.auth.data.repositories.AuthRepository

@Module
abstract class AuthDataModule {

    @Binds
    internal abstract fun authRepository(authRepository: AuthRepository): Any
}