package com.sudox.android.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.sudox.android.data.models.contacts.dto.ContactInfoDTO
import com.sudox.android.data.models.users.dto.UserInfoDTO

@Entity(tableName = "contacts")
class Contact {

    @PrimaryKey
    lateinit var uid: String

    @ColumnInfo
    lateinit var name: String

    @ColumnInfo
    lateinit var nickname: String

    @ColumnInfo
    lateinit var photo: String

    /**
     * Генерирует строку из 1-х букв имени и фамилии
     *
     * Например:
     * Полное имя: Максим Митюшкин
     * Короткое: ММ
     **/
    fun buildShortName(): String {
        val builder = StringBuilder()
        val names = name.split(" ")

        // Билдим имя
        if (names.isNotEmpty()) builder.append(names[0][0])
        if (names.size >= 2) builder.append(names[1][0])

        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (nickname != other.nickname) return false
        if (photo != other.photo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + photo.hashCode()
        return result
    }

    companion object {
        val TRANSFORMATION_FROM_CONTACT_INFO_DTO: (ContactInfoDTO) -> (Contact) = {
            Contact().apply {
                uid = it.id
                name = it.name
                nickname = it.nickname
                photo = it.photo
            }
        }

        val TRANSFORMATION_FROM_USER_INFO_DTO: (UserInfoDTO) -> (Contact) = {
            Contact().apply {
                uid = it.id
                name = it.name
                nickname = it.nickname
                photo = it.photo
            }
        }
    }
}