package com.odevlibertario.satspass.util

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

fun String.validateEmail(): String {
    if(!this.matches(emailRegex)) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email")
    }
    return this
}

fun String.validateNotEmpty(): String {
    if(this.isEmpty()) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty String")
    }

    return this
}