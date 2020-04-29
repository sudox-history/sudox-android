package ru.sudox.android.auth.data.entities

/**
 * Стадии авторизации.
 */
enum class AuthSessionStage {
    PHONE_ENTERED,
    CODE_ENTERED,
    VERIFY_CALLED,
    VERIFY_COMPLETE,
    AUTHORIZED,
    REGISTERED
}