package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.TicketAndEvent
import com.odevlibertario.satspass.model.UpsertEventRequest
import com.odevlibertario.satspass.model.UpsertTicketCategoryRequest
import com.odevlibertario.satspass.model.ValidateTicketRequest
import com.odevlibertario.satspass.service.EventService
import com.odevlibertario.satspass.service.TicketService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager")
class ManagerController {
    @Autowired
    private lateinit var ticketService: TicketService

    @Autowired
    lateinit var eventService: EventService

    @PostMapping("/events")
    fun addEvent(@RequestBody request: UpsertEventRequest): ResponseEntity<*> {
        val eventId = eventService.addEvent(request)
        return ok(mapOf("eventId" to eventId))
    }

    @PutMapping("/events/{eventId}")
    fun updateEvent(@PathVariable eventId: String, @RequestBody request: UpsertEventRequest): ResponseEntity<*> {
        eventService.updateEvent(eventId, request)
        return ok(null)
    }

    @PutMapping("/events/{eventId}/publish")
    fun publishEvent(@PathVariable eventId: String) : ResponseEntity<*> {
        eventService.publishEvent(eventId)
        return ok(null)
    }

    @GetMapping("/events")
    fun getEventsFromCurrentUser() : ResponseEntity<*> {
        return ok(eventService.getEventsFromCurrentUser())
    }

    @GetMapping("/events/{eventId}")
    fun getEvent(@PathVariable eventId: String) : ResponseEntity<*> {
        return ok(eventService.getEventWithTicketCategories(eventId))
    }

    @GetMapping("/events/{eventId}/statistics")
    fun getEventStatistics(@PathVariable eventId: String) : ResponseEntity<*> {
        return ok(eventService.getEventStatistics(eventId))
    }

    @PostMapping("events/{eventId}/ticket-categories")
    fun addTicketCategory(@PathVariable eventId: String, @RequestBody request: UpsertTicketCategoryRequest): ResponseEntity<*> {
        ticketService.addTicketCategory(eventId, request)
        return ok(null)
    }
    @GetMapping("/events/{eventId}/ticket-categories")
    fun getTicketCategories(@PathVariable eventId: String) : ResponseEntity<*> {
        return ok(ticketService.getTicketCategories(eventId))
    }
    @PutMapping("/events/{eventId}/ticket-categories/{ticketCategoryId}")
    fun updateTicketCategory(@PathVariable eventId: String, @PathVariable ticketCategoryId: String, @RequestBody request: UpsertTicketCategoryRequest): ResponseEntity<*> {
        ticketService.updateTicketCategory(eventId, ticketCategoryId, request)
        return ok(null)
    }
    @DeleteMapping("/events/{eventId}/ticket-categories/{ticketCategoryId}")
    fun deleteTicketCategory(@PathVariable eventId: String, @PathVariable ticketCategoryId: String): ResponseEntity<*> {
        ticketService.deleteTicketCategory(eventId, ticketCategoryId)
        return ok(null)
    }
    @DeleteMapping("/events/{eventId}")
    fun deleteEvent(@PathVariable eventId: String) : ResponseEntity<*> {
        eventService.deleteEvent(eventId)
        return ok(null)
    }

    @PostMapping("/tickets/{ticketId}/validate")
    fun validateTicket(@PathVariable ticketId: String) {
        return ticketService.validateTicket(ticketId)
    }

    @PostMapping("/tickets/qr-code-info")
    fun getQrCodeInfo(@RequestBody validateTicketRequest: ValidateTicketRequest): TicketAndEvent {
        return ticketService.getQrCodeInfo(validateTicketRequest.qrCode)
    }
}