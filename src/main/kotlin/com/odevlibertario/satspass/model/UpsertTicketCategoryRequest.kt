package com.odevlibertario.satspass.model

import java.time.Instant

data class UpsertTicketCategoryRequest (val categoryName: String,
                                        val price: Int,
                                        val currency: Currency,
                                        val quantity: Int,
                                        val salesStartDate : Instant,
                                        val salesEndDate : Instant? = null){

}
