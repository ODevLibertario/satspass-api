package com.odevlibertario.satspass.model

import java.time.Instant


data class TicketCategory (
    val id: String,
    val eventId: String,
    val categoryName: String,
    val price: Int,
    val currency: Currency,
    val quantity: Int,
    val salesStartDate : Instant,
    val salesEndDate : Instant? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)
