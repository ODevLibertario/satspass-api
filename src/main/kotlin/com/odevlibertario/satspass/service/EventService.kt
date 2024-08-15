package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.EventDao
import com.odevlibertario.satspass.dao.TicketDao
import com.odevlibertario.satspass.model.*
import com.odevlibertario.satspass.util.getCurrentUser
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class EventService(
    val eventDao: EventDao,
    private val ticketDao: TicketDao
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
            publicityImageUrl = request.publicityImageUrl,
            EventStatus.DRAFT
        )
        )
    }

    private fun validateEvent(request: UpsertEventRequest) {
        if (request.startDate < Instant.now()) {
            throw IllegalArgumentException("A data de início do evento não pode ser no passado")
        }
        if (request.endDate < request.startDate) {
            throw IllegalArgumentException("Data do fim do evento, não pode ser antes da data do começo do evento")
        }
    }

    fun isPublished(eventId: String): Boolean{
        val event = eventDao.getEvent(eventId)
        if(event == null){
            throw IllegalArgumentException("O evento não existe")
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
             throw IllegalArgumentException("O evento publicado não pode ser deletado")
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