package com.odevlibertario.satspass.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider {

    @Value("\${security.jwt.token.secret-key}")
    private lateinit var secretKey: String

    @Value("\${security.jwt.token.expire-length}")
    private var validityInMilliseconds: Long = 0

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    fun createToken(email: String): String {
        val claims: Claims = Jwts.claims().subject(email).build()
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)
        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(validity)
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(getEmail(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getEmail(token: String): String {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray())).build().parseSignedClaims(token).payload.subject
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray())).build().parseSignedClaims(token)
            !claims.payload.expiration.before(Date())
        } catch (e: JwtException) {
            throw BadCredentialsException("Expired or invalid JWT token")
        }
    }
}
