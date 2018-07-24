package com.sudox.android.di

import com.sudox.android.ApplicationLoader
import com.sudox.android.di.module.AndroidInjectorActivityBindingModule
import com.sudox.android.di.module.AppModule
import com.sudox.android.di.module.ViewModelModule
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.ProtocolHandshake
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AndroidInjectorActivityBindingModule::class),
    (AppModule::class),
    (ViewModelModule::class)])
interface AppComponent {


    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ApplicationLoader): Builder

        fun build(): AppComponent
    }

    fun inject(app: ApplicationLoader)

    fun inject(protocolClient: ProtocolClient)

    fun inject(handshake: ProtocolHandshake)
}