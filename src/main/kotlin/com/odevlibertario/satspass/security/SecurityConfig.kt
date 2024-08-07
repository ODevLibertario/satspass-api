package com.odevlibertario.satspass.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class SecurityConfig @Autowired constructor(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(authConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        val encoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
        encoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256)
        return encoder
    }

    @Bean
    @Primary
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): HttpSecurity {
        return http.sessionManagement { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.ignoringRequestMatchers("/**") }
            .authorizeHttpRequests{requests ->
                requests.requestMatchers("/auth/**").permitAll()
                requests.requestMatchers(AntPathRequestMatcher("/user/**")).hasAnyAuthority("EVENT_CUSTOMER", "EVENT_MANAGER", "ADMIN")
                requests.requestMatchers(AntPathRequestMatcher("/customer/**")).hasAuthority("EVENT_CUSTOMER")
                requests.requestMatchers(AntPathRequestMatcher("/manager/**")).hasAuthority("EVENT_MANAGER")
                requests.requestMatchers(AntPathRequestMatcher("/admin/**")).hasAuthority("ADMIN")
            }.with(JwtConfigurer(jwtTokenProvider), Customizer.withDefaults())
    }
}