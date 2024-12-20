package com.odevlibertario.satspass.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.odevlibertario.satspass.model.BitcoinInvoice
import org.apache.coyote.BadRequestException


import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

val objectMapper: ObjectMapper = ObjectMapper()

@Service
class BitcoinService {

    @Value("\${bitcoin.invoice.url}")
    private lateinit var bitcoinInvoiceUrl: String

    @Value("\${bitcoin.invoice.api-key}")
    private lateinit var bitcoinInvoiceApiKey: String

    private val httpClient = HttpClient.newHttpClient()

    fun generateInvoice(sats: Int): BitcoinInvoice {
        val body = objectMapper.createObjectNode()

        body.put("out", false)
        body.put("amount", sats)
        body.put("memo", "Pagamento satspass")

        val request = HttpRequest.newBuilder()
            .headers("X-Api-Key", bitcoinInvoiceApiKey, "Content-Type", "application/json")
            .uri(URI("$bitcoinInvoiceUrl/api/v1/payments"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if(response.statusCode() == 201) {
            val invoiceJson = objectMapper.readTree(response.body())
            return BitcoinInvoice(invoiceJson["payment_hash"].asText(), invoiceJson["payment_request"].asText())
        }else{
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao gerar invoice")
        }
    }
}
