package ru.sudox.android.auth.data.entities

/**
 * Стадии авторизации.
 */
enum class AuthSessionStage {
    PHONE_CHECKED,
    CODE_CHECKED,
    VERIFIED
}