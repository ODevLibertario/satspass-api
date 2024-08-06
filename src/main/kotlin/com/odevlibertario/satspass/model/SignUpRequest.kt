package com.odevlibertario.satspass.model

data class SignUpRequest(
    val email: String,
    val username: String?,
    val password: String
)