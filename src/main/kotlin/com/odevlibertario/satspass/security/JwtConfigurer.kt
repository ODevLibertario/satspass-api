package com.odevlibertario.satspass.security

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@EnableWebSecurity
class JwtConfigurer(private val jwtTokenProvider: JwtTokenProvider) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(
            JwtTokenFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter::class.java
        )
    }
}