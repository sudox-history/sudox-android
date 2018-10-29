package com.sudox.android.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.sudox.android.data.models.chats.UserChatRecipient
import com.sudox.android.data.models.contacts.dto.ContactInfoDTO
import com.sudox.android.data.models.users.dto.UserInfoDTO
import com.sudox.android.data.models.users.dto.UsersGetByEmailDTO

@Entity(tableName = "contacts")
class User {

    @PrimaryKey
    lateinit var uid: String

    @ColumnInfo
    lateinit var name: String

    @ColumnInfo
    lateinit var nickname: String

    @ColumnInfo
    lateinit var avatar: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (nickname != other.nickname) return false
        if (avatar != other.avatar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + avatar.hashCode()
        return result
    }

    companion object {
        val TRANSFORMATION_FROM_CONTACT_INFO_DTO: (ContactInfoDTO) -> (User) = {
            User().apply {
                uid = it.id
                name = it.name
                nickname = it.nickname
                avatar = it.photo
            }
        }

        val TRANSFORMATION_FROM_USER_INFO_DTO: (UserInfoDTO) -> (User) = {
            User().apply {
                uid = it.id
                name = it.name
                nickname = it.nickname
                avatar = it.photo
            }
        }

        val TRANSFORMATION_FROM_USER_GET_BY_EMAIL_DTO: (UsersGetByEmailDTO) -> (User) = {
            User().apply {
                uid = it.id
                name = it.name
                nickname = it.nickname
                avatar = it.photo
            }
        }

        val TRANSFORMATION_TO_USER_CHAT_RECIPIENT: (User) -> (UserChatRecipient) = {
            UserChatRecipient().apply {
                uid = it.uid
                name = it.name
                nickname = it.nickname
                photo = it.avatar
            }
        }
    }
}