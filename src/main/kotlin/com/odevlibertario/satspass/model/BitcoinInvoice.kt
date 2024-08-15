package com.odevlibertario.satspass.model

data class BitcoinInvoice(
    val paymentHash: String,
    val paymentRequest: String
)