package com.sudox.android.common.models

import com.sudox.android.common.enums.TokenState

data class SecretData(val tokenState: TokenState, val id: String? = null)