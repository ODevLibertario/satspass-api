package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.SignInRequest
import com.odevlibertario.satspass.model.SignUpRequest
import com.odevlibertario.satspass.model.VerifyRequest
import com.odevlibertario.satspass.security.JwtTokenProvider
import com.odevlibertario.satspass.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customer")
class CustomerController {

    @GetMapping("/hello-world")
    fun signIn(): ResponseEntity<*> {
        return ok("Hello world")
    }
}