package com.odevlibertario.satspass.model

import java.time.Instant

data class User(
    val id: String,
    val email: String,
    val username: String?,
    val password: String,
    val userStatus: UserStatus,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)