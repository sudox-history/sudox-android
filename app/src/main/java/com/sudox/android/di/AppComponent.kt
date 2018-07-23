package com.sudox.android.di

import com.sudox.android.ApplicationLoader
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AndroidInjectorActivityBindingModule::class),
    (AppModule::class)])
interface AppComponent {


    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ApplicationLoader): Builder

        fun build(): AppComponent
    }

    fun inject(app: ApplicationLoader)
}