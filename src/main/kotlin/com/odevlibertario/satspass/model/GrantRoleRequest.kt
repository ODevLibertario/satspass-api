package com.odevlibertario.satspass.model

data class GrantRoleRequest(
               val userId: String,
               val role: UserRole
)