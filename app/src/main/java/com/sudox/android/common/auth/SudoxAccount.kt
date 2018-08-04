package com.sudox.android.common.auth

// Account id key
const val KEY_ACCOUNT_ID = "ACCOUNT_ID"

data class SudoxAccount(var id: Long,
                        var name: String,
                        var token: String)