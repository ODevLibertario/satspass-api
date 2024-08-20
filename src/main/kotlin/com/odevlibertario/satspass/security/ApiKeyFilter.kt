package com.odevlibertario.satspass.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import org.springframework.web.server.ResponseStatusException
import java.io.IOException

class ApiKeyFilter(private val apiKey: String) : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {
        val httpRequest = req as HttpServletRequest
        val requestApiKey = httpRequest.getHeader("X-api-key")

        if (requestApiKey == null || requestApiKey != apiKey ) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
        filterChain.doFilter(req, res)
    }
}
