package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.*
import com.odevlibertario.satspass.security.JwtTokenProvider
import com.odevlibertario.satspass.service.BitcoinService
import com.odevlibertario.satspass.service.EventService
import com.odevlibertario.satspass.service.TicketService
import com.odevlibertario.satspass.service.UserService
import jakarta.websocket.server.PathParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
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
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var ticketService: TicketService


    @PostMapping("/events/{eventId}/ticket-categories/{ticketCategoryId}/buy-ticket")
    fun buyTicket(@PathVariable eventId: String, @PathVariable ticketCategoryId: String): ResponseEntity<String> {
        return ok(ticketService.buyTicket(eventId, ticketCategoryId))
    }

    @GetMapping("/events")
    fun getEvents(): ResponseEntity<List<Event>> {
        return ok(eventService.getPublishedEvents())
    }

    @GetMapping("/{userId}/tickets")
    fun getTickets(@PathVariable userId: String): ResponseEntity<List<Ticket>> {
        return ok(ticketService.getTicket(userId))
    }



}