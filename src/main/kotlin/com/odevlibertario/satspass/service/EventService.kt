package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.EventDao
import com.odevlibertario.satspass.dao.TicketDao
import com.odevlibertario.satspass.model.*
import com.odevlibertario.satspass.util.getCurrentUser
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID

@Service
class EventService(
    val eventDao: EventDao
) {
    fun addEvent(request: UpsertEventRequest) {
        validateEvent(request)

        eventDao.addEvent(
            Event(
                id = UUID.randomUUID().toString(),
                managerId = getCurrentUser().id,
                name = request.name,
                startDate = request.startDate,
                endDate = request.endDate,
                startTime = request.startTime,
                endTime = request.endTime,
                description = request.description,
                location = request.location,
                publicityImageUrl = request.publicityImageUrl,
                EventStatus.DRAFT
            )
        )
    }

    private fun validateEvent(request: UpsertEventRequest) {
        if (request.startDate < Instant.now()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de início do evento não pode ser no passado")
        }
        if (request.endDate < request.startDate) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Data do fim do evento, não pode ser antes da data do começo do evento")
        }
    }

    fun isPublished(eventId: String): Boolean{
        val event = eventDao.getEvent(eventId)
        if(event == null){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O evento não existe")
        }
        return event.eventStatus == EventStatus.PUBLISHED
    }
    fun updateEvent(eventId: String, request: UpsertEventRequest) {
        validateEvent(request)
        eventDao.updateEvent(eventId, request)
    }

    fun publishEvent(eventId: String) {
        eventDao.publishEvent(eventId)

    }

    fun getEventsFromCurrentUser(): List<Event> {
       return eventDao.getEvents(getCurrentUser().id)
    }

    fun deleteEvent(eventId: String) {
         if(isPublished(eventId)){
             throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O evento publicado não pode ser deletado")
         }
        eventDao.deleteEvent(eventId)
    }
    fun getEvent(eventId: String): Event? {
        return eventDao.getEvent(eventId)
    }

    fun getPublishedEvents(): List<Event> {
        return eventDao.getPublishedEvents()
    }

}