package com.sudox.android.ui.auth.confirm.enums

enum class AuthConfirmAction {
    SHOW_REGISTER_FRAGMENT,
    SHOW_PHONE_FRAGMENT_WITH_CODE_EXPIRED_ERROR,
    SHOW_PHONE_FRAGMENT_WITH_TOO_MANY_REQUESTS,
    FREEZE,
    UNFREEZE
}