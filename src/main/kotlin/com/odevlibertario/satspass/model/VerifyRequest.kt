package com.odevlibertario.satspass.model

data class VerifyRequest(
    val email: String,
    val code: String
)