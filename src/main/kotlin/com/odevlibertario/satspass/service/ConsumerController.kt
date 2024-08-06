package com.odevlibertario.satspass.service

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Collectors.toList

@RestController
class ConsumerController {
    @GetMapping("/me")
    fun currentUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<*> {
        val model: MutableMap<Any, Any> = HashMap()
        model["username"] = userDetails.username
        model["roles"] = userDetails.authorities
            .stream()
            .map { a: GrantedAuthority -> a.authority }
            .collect(toList())
        return ok(model)
    }
}