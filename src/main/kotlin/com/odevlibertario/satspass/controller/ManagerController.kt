package com.odevlibertario.satspass.controller

import com.odevlibertario.satspass.model.UpsertEventRequest
import com.odevlibertario.satspass.service.EventService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager")
class ManagerController {
    @Autowired
    lateinit var eventService: EventService

    @PostMapping("/events")
    fun addEvent(@RequestBody request: UpsertEventRequest): ResponseEntity<*> {
        eventService.addEvent(request)
        return ok(null)
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
    fun getEvents() : ResponseEntity<*> {
        return ok(eventService.getEvents())
    }
}