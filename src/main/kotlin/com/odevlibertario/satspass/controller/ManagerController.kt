package com.odevlibertario.satspass.controller

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager")
class ManagerController {

    @PostMapping("/events")
    fun addEvent(): ResponseEntity<*> {
        return ok("Hello world")
    }
}