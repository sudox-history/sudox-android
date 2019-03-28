package com.sudox.android.common.di

import com.sudox.android.ApplicationLoader
import com.sudox.android.common.di.module.AppModule
import com.sudox.android.common.di.module.DatabaseModule
import com.sudox.android.common.di.module.ViewModelModule
import com.sudox.android.common.di.module.activities.ActivityModule
import com.sudox.android.data.repositories.RepositoriesContainer
import com.sudox.android.ui.auth.common.BaseAuthFragment
import com.sudox.design.navigation.toolbar.SudoxToolbar
import com.sudox.protocol.ProtocolClient
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AndroidSupportInjectionModule::class),
    (AppModule::class),
    (ActivityModule::class),
    (DatabaseModule::class),
    (ViewModelModule::class)])
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun protocolClient(): ProtocolClient
    fun repositoriesContainer(): RepositoriesContainer
    fun inject(app: ApplicationLoader)
    fun inject(baseAuthFragment: BaseAuthFragment)
    fun inject(sudoxToolbar: SudoxToolbar)

    // Не знаю зачем это здесь написали, но без него не работает
    override fun inject(instance: DaggerApplication?)
}