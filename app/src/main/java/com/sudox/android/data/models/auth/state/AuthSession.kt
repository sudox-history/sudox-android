package com.sudox.android.data.models.auth.state

// Тут нет особого смысла использовать Enum, т.к. это только усложнит код

class AuthSession(val phoneNumber: String, val hash: String, val status: Int = -1, var code: String? = null) {

    // А может в будущем будут ещё переменные с таким названием, а? :)
    companion object {
        const val AUTH_STATUS_NOT_REGISTERED = 0
        const val AUTH_STATUS_REGISTERED = 1
    }
}