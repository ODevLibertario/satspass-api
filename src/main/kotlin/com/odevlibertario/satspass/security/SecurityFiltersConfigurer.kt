package com.odevlibertario.satspass.security


import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
class SecurityFiltersConfigurer(
    private val jwtTokenProvider: JwtTokenProvider,
    private val apiKey: String
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.addFilterAfter(ApiKeyFilter(apiKey),
            UsernamePasswordAuthenticationFilter::class.java)

        http.addFilterBefore(
            JwtTokenFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter::class.java
        )
    }
}