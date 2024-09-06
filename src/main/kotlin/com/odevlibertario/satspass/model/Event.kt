package com.odevlibertario.satspass.model

import java.time.Instant

data class Event(
    val id: String,
    val managerId: String,
    val name: String,
    val startDate: Instant,
    val endDate: Instant,
    val startTime: Instant,
    val endTime: Instant,
    val location: String?,
    val publicityImageUrl: String?,
    val eventStatus: EventStatus,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)