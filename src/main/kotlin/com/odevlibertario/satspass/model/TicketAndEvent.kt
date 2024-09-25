package com.odevlibertario.satspass.model

import java.time.Instant

class TicketAndEvent(
    val categoryName: String,
    val qrCode: String?,
    val invoice: String,
    val ticketStatus: TicketStatus,
    val eventName: String,
    val eventStartDate: Instant,
    val eventEndDate: Instant,
    val eventStartTime: Instant,
    val eventEndTime: Instant,
    val eventDescription: String?,
    val eventLocation: String?,
    val eventPublicityImageUrl: String?,
)