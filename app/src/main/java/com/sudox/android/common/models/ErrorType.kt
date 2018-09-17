package com.sudox.android.common.models

enum class ErrorType(private val code: Int) {
    INVALID_PARAMETERS(50),
    INVALID_USER(51),
    INVALID_IMPORT_DATA(203),
    WRONG_CODE(201),
    INVALID_ACCOUNT(202),
    EMPTY_CONTACTS_LIST(400),;

    companion object {
        fun findByCode(code: Int) = values().find { it.code == code }
    }
}