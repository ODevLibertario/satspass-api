package com.odevlibertario.satspass.model

data class Ticket ( val id: String,
                    val eventId: String,
                    val ticketCategoryId: String,
                    val userId: String,
                    val qrCode: String?,
                    val statusTicket: TicketStatus,
                    val paymentHash: String
)