package com.sudox.android.common.models

import com.sudox.android.common.enums.EmailState

data class SendCodeData(val state: EmailState, val hash: String? = null, val status: Int? = null)