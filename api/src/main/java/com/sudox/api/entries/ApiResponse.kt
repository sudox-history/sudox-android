package com.sudox.api.entries

data class ApiResponse<T>(
        val methodName: String,
        val result: Int,
        val data: T
)