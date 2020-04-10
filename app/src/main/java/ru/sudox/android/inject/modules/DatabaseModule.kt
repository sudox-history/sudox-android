package ru.sudox.android.inject.modules

import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.sudox.android.AppEncryptor

@Module
class DatabaseModule(
        private val name: String,
        private val schemaVersion: Long
) {

    @Provides
    fun providesRealm(encryptor: AppEncryptor): Realm {
        return Realm.getInstance(RealmConfiguration.Builder()
                .name(name)
                .schemaVersion(schemaVersion)
                .encryptionKey(encryptor.getDatabaseKey())
                .build())
    }
}