package com.sudox.android.di

import android.app.Application
import com.sudox.android.ApplicationLoader
import com.sudox.android.di.module.*
import com.sudox.android.di.module.activities.ActivityModule
import dagger.BindsInstance
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
    (AccountModule::class),
    (ProtocolModule::class),
    (DatabaseModule::class),
    (DataModule::class),
    (ViewModelModule::class)])
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(app: ApplicationLoader)

    override fun inject(instance: DaggerApplication?)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }
}