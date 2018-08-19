package com.sudox.android.common

import com.sudox.android.common.enums.State

data class Data<out T>(val data: T, val description: String? = null)
data class StateData<out T>(val data: T?, val state: State)