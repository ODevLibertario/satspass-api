package com.odevlibertario.satspass.config

import com.odevlibertario.satspass.service.objectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<String> {
        logger.error(ex.message, ex)

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