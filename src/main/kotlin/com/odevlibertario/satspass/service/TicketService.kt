package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.TicketDao
import com.odevlibertario.satspass.model.*
import com.odevlibertario.satspass.util.getCurrentUser
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
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
                salesStartDate = request.salesStartDate,
                salesEndDate = request.salesEndDate
            )
        )
    }

    private fun validateCategory(request: UpsertTicketCategoryRequest) {
        if (request.salesEndDate != null && request.salesEndDate < request.salesStartDate) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de fim não pode ser menor que a data de ínicio das vendas")
        }
        if (request.price < 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O preço não pode ser menor do que 0")
        }
        if(request.quantity < 0){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade não pode ser menor do que 0")
        }

    }

    private fun validateEvent(eventId: String) {

        if (eventService.isPublished(eventId)){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível adicionar categoria em um evento publicado")
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

    @Transactional
    fun buyTicket(eventId: String, ticketCategoryId: String): String {
        val event = eventService.getEvent(eventId)
        val currentUserId = getCurrentUser().id

        if(event == null || event.eventStatus == EventStatus.DRAFT){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O evento está inválido")
        }
        val ticketCategory = ticketDao.getTicketCategory(ticketCategoryId)
        ticketDao.lockTable("satspass.ticket", "ACCESS EXCLUSIVE")
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
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Não há mais ingressos disponíveis para essa categoria")
        }
    }

    fun getTicket(userId: String): List<Ticket> {
        return ticketDao.getTickets(userId)
    }

    fun validateQrCode(ticketId: String, request: ValidateQrCodeRequest): Boolean {
       val ticket = ticketDao.getTicket(ticketId)
        val result = ticket != null && ticket.ticketStatus == TicketStatus.PURCHASED && ticket.qrCode == request.qrCode
        if(result){
            ticketDao.updateTicket(ticketId, TicketStatus.USED)
        }
        return result
    }
}