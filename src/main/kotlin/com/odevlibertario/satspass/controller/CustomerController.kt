package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.Event
import com.odevlibertario.satspass.model.SelectedTicket
import com.odevlibertario.satspass.model.Ticket
import com.odevlibertario.satspass.service.EventService
import com.odevlibertario.satspass.service.TicketService
import com.odevlibertario.satspass.service.objectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customer")
class CustomerController {

    @Autowired
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var ticketService: TicketService

    @PostMapping("/events/{eventId}/buy-tickets")
    fun buyTickets(@PathVariable eventId: String, @RequestBody selectedTickets: List<SelectedTicket>): ResponseEntity<*> {
        val invoice = ticketService.buyTickets(eventId, selectedTickets)

        val body = objectMapper.createObjectNode()

        body.put("invoice", invoice)

        return ok(body)
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