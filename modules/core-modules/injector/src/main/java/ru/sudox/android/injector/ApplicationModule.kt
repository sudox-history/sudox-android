package ru.sudox.android.injector

import android.app.Application
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.sudox.phone.AssetsMetadataLoader
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun providePhoneNumberUtil(app: Application): PhoneNumberUtil = PhoneNumberUtil.createInstance(AssetsMetadataLoader(app.assets))
}