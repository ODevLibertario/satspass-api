package com.odevlibertario.satspass.config

import com.odevlibertario.satspass.service.objectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<String> {
        val errorMessage = objectMapper.createObjectNode()

        if(ex is ResponseStatusException) {
            errorMessage.put("message", ex.reason)
            return ResponseEntity(objectMapper.writeValueAsString(errorMessage), ex.statusCode)
        } else {
            errorMessage.put("message", ex.message)
        }
        return ResponseEntity(objectMapper.writeValueAsString(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}