package ru.sudox.android.account.entries

/**
 * Обьект с информацией об аккаунте.
 * В дальнейшем сериализуется с помощью Jackson.
 *
 * @param id ID аккаунта
 * @param name Имя пользователя
 * @param nickname Никнейм пользователя
 * @param token Токен сессии пользователя
 * @param key Ключ пользователя
 */
data class AccountData(
        val id: String,
        val name: String,
        val nickname: String,
        val token: String,
        val key: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountData

        if (name != other.name) return false
        if (token != other.token) return false
        if (!key.contentEquals(other.key)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + key.contentHashCode()
        return result
    }
}