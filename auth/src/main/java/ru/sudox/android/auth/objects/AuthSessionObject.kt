package ru.sudox.android.auth.objects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

@Suppress("unused")
open class AuthSessionObject : RealmObject {

    @PrimaryKey
    var phone: String = ""
    var token: String = ""
    var userExists: Boolean = false

    constructor() : super()
    constructor(phone: String, token: String, userExists: Boolean) : super() {
        this.phone = phone
        this.token = token
        this.userExists = userExists
    }
}