package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.EventDao
import com.odevlibertario.satspass.dao.TicketDao
import com.odevlibertario.satspass.model.EventStatus
import com.odevlibertario.satspass.model.TicketCategory
import com.odevlibertario.satspass.model.UpsertEventRequest
import com.odevlibertario.satspass.model.UpsertTicketCategoryRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TicketService(
    val ticketDao: TicketDao,
    val eventService: EventService
) {
    fun addTicketCategory(eventId: String, request: UpsertTicketCategoryRequest) {
        validateCategory(request)

        ticketDao.addTicketCategory(
            TicketCategory(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                categoryName = request.categoryName,
                price = request.price,
                currency = request.currency,
                quantity = request.quantity,
                salesStarDate = request.salesStartDate,
                salesEndDate = request.salesEndDate
            )
        )
    }

    private fun validateCategory(request: UpsertTicketCategoryRequest) {
        if (request.salesEndDate != null && request.salesEndDate < request.salesStartDate) {
            throw IllegalArgumentException("A data de fim não pode ser menor que a data de ínicio das vendas")
        }
        if (request.price < 0) {
            throw IllegalArgumentException("O preço não pode ser menor do que 0")
        }
        if(request.quantity < 0){
            throw IllegalArgumentException("A quantidade não pode ser menor do que 0")
        }

    }

    private fun validateEvent(eventId: String) {

        if (eventService.isPublished(eventId)){
            throw IllegalArgumentException("Não é possível adicionar categoria em um evento publicado")
        }
    }
    fun getTicketCategories(eventId: String): List<TicketCategory> {
        return ticketDao.getTicketCategories(eventId)
    }

    fun updateTicketCategory(eventId: String, ticketCategoryId: String, request: UpsertTicketCategoryRequest ) {
        validateEvent(eventId)
        validateCategory(request)
        ticketDao.updateTicketCategory(eventId, ticketCategoryId, request)
    }

    fun deleteTicketCategory(eventId: String, ticketCategoryId: String) {
        validateEvent(eventId)
        ticketDao.deleteTicketCategory(ticketCategoryId)
    }


}