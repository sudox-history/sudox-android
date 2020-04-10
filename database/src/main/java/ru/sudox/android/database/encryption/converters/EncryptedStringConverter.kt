package ru.sudox.android.database.encryption.converters

import io.objectbox.converter.PropertyConverter
import ru.sudox.android.database.encryption.DatabaseEncryptor

class EncryptedStringConverter : PropertyConverter<String, ByteArray> {

    override fun convertToDatabaseValue(entityProperty: String): ByteArray {
        return DatabaseEncryptor.encryptData(entityProperty.toByteArray())
    }

    override fun convertToEntityProperty(databaseValue: ByteArray): String {
        return String(DatabaseEncryptor.decryptData(databaseValue))
    }
}