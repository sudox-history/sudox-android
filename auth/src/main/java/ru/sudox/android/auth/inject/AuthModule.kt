package ru.sudox.android.auth.inject

import dagger.Module
import ru.sudox.android.auth.data.inject.AuthDataModule
import ru.sudox.android.auth.ui.inject.AuthUiModule

@Module(includes = [
    AuthUiModule::class,
    AuthDataModule::class
])
abstract class AuthModule