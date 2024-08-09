package com.odevlibertario.satspass.model

import java.time.Instant

data class UpsertEventRequest(val name: String,
                              val startDate: Instant,
                              val endDate: Instant,
                              val publicityImageUrl: String?) {

}