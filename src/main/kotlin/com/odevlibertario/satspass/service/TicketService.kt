package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.EventDao
import com.odevlibertario.satspass.dao.TicketDao
import com.odevlibertario.satspass.model.*
import com.odevlibertario.satspass.util.getCurrentUser
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TicketService(
    val ticketDao: TicketDao,
    val eventService: EventService,
    val bitcoinService: BitcoinService
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

    fun buyTicket(eventId: String, ticketCategoryId: String): String {
        val event = eventService.getEvent(eventId)
        val currentUserId = getCurrentUser().id

        if(event == null || event.eventStatus == EventStatus.DRAFT){
            throw IllegalArgumentException ("O evento está inválido")
        }
        val ticketCategory = ticketDao.getTicketCategory(ticketCategoryId)
        val count = ticketDao.getCountForTickerCategory(ticketCategoryId)
        if (count < ticketCategory.quantity){
            val invoice = bitcoinService.generateInvoice(ticketCategory.price)
            ticketDao.addTicket(Ticket(
                UUID.randomUUID().toString(),
                eventId,
                ticketCategoryId,
                currentUserId,
                null,
                TicketStatus.RESERVED,
                invoice.paymentHash))
            return invoice.paymentRequest
        }else{
            throw IllegalArgumentException("Não há mais ingressos disponíveis para essa categoria")
        }
    }


}