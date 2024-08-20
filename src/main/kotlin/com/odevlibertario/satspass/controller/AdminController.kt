package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.GrantRoleRequest
import com.odevlibertario.satspass.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/admin")
class AdminController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping("/grant-role")
    fun grantRole(@RequestBody grantRoleRequest: GrantRoleRequest): ResponseEntity<*> {
       userService.grantRole(grantRoleRequest)
       return ok(null)
    }
}