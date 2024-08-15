package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.SignInRequest
import com.odevlibertario.satspass.model.SignUpRequest
import com.odevlibertario.satspass.model.VerifyRequest
import com.odevlibertario.satspass.security.JwtTokenProvider
import com.odevlibertario.satspass.service.BitcoinService
import com.odevlibertario.satspass.service.TicketService
import com.odevlibertario.satspass.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customer")
class CustomerController {

    @Autowired
    private lateinit var ticketService: TicketService


    @PostMapping("/events/{eventId}/ticket-categories/{ticketCategoryId}/buy-ticket")
    fun buyTicket(@PathVariable eventId: String, @PathVariable ticketCategoryId: String): ResponseEntity<String> {
        return ok(ticketService.buyTicket(eventId, ticketCategoryId))
    }
}