package com.sudox.android.common.models

import com.sudox.android.common.enums.SignUpInState

data class SignUpInData(val state: SignUpInState, val id: String? = null, val secret: String? = null)
