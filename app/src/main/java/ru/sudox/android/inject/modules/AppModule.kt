package ru.sudox.android.inject.modules

import dagger.Binds
import dagger.Module
import ru.sudox.android.AppConnector
import ru.sudox.android.AppEncryptor

@Module
abstract class AppModule {

    @Binds
    abstract fun connector(appConnector: AppConnector): Any

    @Binds
    abstract fun encryptor(appEncryptor: AppEncryptor): Any
}