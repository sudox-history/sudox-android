package ru.sudox.api.users.entries.get

import ru.sudox.api.users.entries.UserDTO

data class UsersGetResponseDTO(
        var users: Array<UserDTO>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsersGetResponseDTO

        if (!users.contentEquals(other.users)) return false

        return true
    }

    override fun hashCode(): Int {
        return users.contentHashCode()
    }
}