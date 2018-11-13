package com.sudox.android.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.sudox.android.data.models.chats.UserChatRecipient
import com.sudox.android.data.models.contacts.dto.ContactInfoDTO
import com.sudox.android.data.models.users.dto.SearchUserDTO
import com.sudox.android.data.models.users.dto.UserInfoDTO

@Entity(tableName = "users")
class User {

    @PrimaryKey
    lateinit var uid: String

    @ColumnInfo
    lateinit var name: String

    @ColumnInfo
    lateinit var nickname: String

    @ColumnInfo
    lateinit var avatar: String

    @ColumnInfo
    var phone: String? = null

    @ColumnInfo
    var status: String? = null

    @ColumnInfo
    var bio: String? = null

    /**
     * 1 - Профиль
     * 2 - Контакт
     * 3 - Неизвестный
     */
    @ColumnInfo
    var type: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (uid != other.uid) return false
        if (phone != other.phone) return false
        if (name != other.name) return false
        if (nickname != other.nickname) return false
        if (avatar != other.avatar) return false
        if (status != other.status) return false
        if (bio != other.bio) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()

        result = 31 * result + name.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + avatar.hashCode()

        if(phone != null)
            result = 31 * result + phone!!.hashCode()

        if(status != null)
            result = 31 * result + status!!.hashCode()

        if(bio != null)
            result = 31 * result + bio!!.hashCode()

        result = 31 * result + type.hashCode()
        return result
    }

    companion object {
        val TRANSFORMATION_FROM_CONTACT_INFO_DTO: (ContactInfoDTO) -> (User) = {
            User().apply {
                uid = it.id
                name = it.name
                nickname = it.nickname
                avatar = it.photo
                status = it.status
                bio = it.bio
                type = 2
            }
        }

        val TRANSFORMATION_FROM_USER_INFO_DTO: (UserInfoDTO) -> (User) = {
            User().apply {
                uid = it.id
                name = it.name
                nickname = it.nickname
                avatar = it.photo
                status = it.status
                bio = it.bio
                type = 2
            }
        }


        @Deprecated("")
        val TRANSFORMATION_FROM_USER_GET_BY_EMAIL_DTO: (SearchUserDTO) -> (User) = {
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