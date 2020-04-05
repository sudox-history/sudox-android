package com.sudox.api.entries.auth

data class AuthCreateResponseBody(
        val authToken: String,
        val userExists: Boolean
)