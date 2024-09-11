package com.odevlibertario.satspass.model

import java.time.Instant

data class UpsertEventRequest(val name: String,
                              val startDate: Instant,
                              val endDate: Instant,
                              val startTime: Instant,
                              val endTime: Instant,
                              val description: String?,
                              val location: String?,
                              val publicityImageUrl: String?)
