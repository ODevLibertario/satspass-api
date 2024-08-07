package com.odevlibertario.satspass.model

data class UpdatePasswordRequest(val email: String, val oldPassword: String, val newPassword: String)