package com.odevlibertario.satspass.util


val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

fun String.validateEmail(): String {
    if(!this.matches(emailRegex)) {
        throw IllegalArgumentException("Invalid email")
    }
    return this
}

fun String.validateNotEmpty(): String {
    if(this.isEmpty()) {
        throw IllegalArgumentException("Empty String")
    }

    return this
}