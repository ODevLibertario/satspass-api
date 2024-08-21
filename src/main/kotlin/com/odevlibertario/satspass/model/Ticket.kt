package com.odevlibertario.satspass.model

import java.time.Instant

data class Ticket (val id: String,
                   val eventId: String,
                   val ticketCategoryId: String,
                   val userId: String,
                   val qrCode: String?,
                   val ticketStatus: TicketStatus,
                   val paymentHash: String,
                   val createdAt: Instant?=null,
                   val updatedAt: Instant?=null
)