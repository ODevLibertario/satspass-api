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
    fun buyTickets(eventId: String, selectedTickets: List<SelectedTicket>): String {
        val event = eventService.getEvent(eventId)
        val currentUserId = getCurrentUser().id

        if(event == null || event.eventStatus == EventStatus.DRAFT){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O evento está inválido")
        }

        val ticketCategories = selectedTickets.map { Pair(ticketDao.getTicketCategory(it.ticketCategoryId), it) }

        ticketDao.lockTable("satspass.ticket", "ACCESS EXCLUSIVE")
        val totalPrice = ticketCategories.map {
            validateTicketAvailability(it.first, it.second)
            it
        }.sumOf { it.first.price * it.second.count }

        val invoice = bitcoinService.generateInvoice(totalPrice)

        selectedTickets.forEach {
            (1..it.count).forEach { _ ->
                ticketDao.addTicket(Ticket(
                    UUID.randomUUID().toString(),
                    eventId,
                    it.ticketCategoryId,
                    currentUserId,
                    null,
                    TicketStatus.RESERVED,
                    invoice.paymentHash,
                    invoice.paymentRequest))
            }
        }

        return invoice.paymentRequest
    }

    private fun validateTicketAvailability(ticketCategory: TicketCategory, selectedTicket: SelectedTicket) {
        val dbCount = ticketDao.getCountForTickerCategory(ticketCategory.id)
        if (ticketCategory.quantity < (selectedTicket.count + dbCount)) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Não há mais ingressos disponíveis para essa categoria"
            )
        }
    }

    fun getTickets(): List<TicketAndEvent> {
        return ticketDao.getTicketsAndEvent(getCurrentUser().id)
    }

    fun validateTicket(ticketId: String) {
        if(ticketDao.getTicket(ticketId)?.ticketStatus == TicketStatus.PURCHASED) {
            ticketDao.updateTicket(ticketId, TicketStatus.USED)
        }
    }

    fun getQrCodeInfo(qrCode: String): TicketAndEvent {
        return ticketDao.getQrCodeInfo(qrCode)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "QR Code inválido")
    }
}