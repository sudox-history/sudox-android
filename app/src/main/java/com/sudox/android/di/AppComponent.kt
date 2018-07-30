package com.sudox.android.di

import com.sudox.android.ApplicationLoader
import com.sudox.android.di.module.*
import com.sudox.android.di.module.ActivityModule
import com.sudox.android.ui.auth.AuthEmailFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AndroidSupportInjectionModule::class),
    (ActivityModule::class),
    (AccountModule::class),
    (AppModule::class),
    (ProtocolModule::class),
    (DataModule::class),
    (ViewModelModule::class)])
interface AppComponent : AndroidInjector<ApplicationLoader> {

    // TODO: Выпилить
    fun inject(authEmailFragment: AuthEmailFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ApplicationLoader): Builder
        fun build(): AppComponent
    }
}