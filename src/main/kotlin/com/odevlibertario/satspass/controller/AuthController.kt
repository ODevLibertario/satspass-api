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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {
    @Autowired
    var authenticationManager: AuthenticationManager? = null

    @Autowired
    var jwtTokenProvider: JwtTokenProvider? = null

    @Autowired
    lateinit var userService: UserService

    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<*> {
        userService.signUp(request)
        return ok(null)
    }

    @PostMapping("/verify-email")
    fun verifyEmail(@RequestBody request: VerifyRequest): ResponseEntity<*> {
        userService.verifyEmail(request)
        return ok(null)
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody data: SignInRequest): ResponseEntity<*> {
        try {
            val email: String = data.email
            authenticationManager!!.authenticate(UsernamePasswordAuthenticationToken(email, data.password))
            val token = jwtTokenProvider!!.createToken(email)
            val model: MutableMap<Any, Any> = HashMap()
            model["token"] = token
            return ok(model)
        } catch (e: AuthenticationException) {
            println(e)
            throw BadCredentialsException("Invalid username/password supplied")
        }
    }
}