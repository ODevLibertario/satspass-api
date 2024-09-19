package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.*
import com.odevlibertario.satspass.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @PutMapping("/password")
    fun updatePassword(@RequestBody request: UpdatePasswordRequest): ResponseEntity<*> {
        userService.updatePassword(request)
        return ok(null)
    }

    @PutMapping("/lightning-address")
    fun updateLightningAddress(@RequestBody request: UpdateLightningAddressRequest): ResponseEntity<*> {
        userService.updateLightningAddress(request)
        return ok(null)
    }

    @GetMapping
    fun getUser(): ResponseEntity<*> {
        return ok(userService.getUser())
    }
}