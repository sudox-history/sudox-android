package ru.sudox.android.account.inject

import ru.sudox.android.account.AccountAuthenticatorService

interface AccountComponent {
    fun inject(service: AccountAuthenticatorService)
}