package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.EventDao
import com.odevlibertario.satspass.dao.UserDao
import com.odevlibertario.satspass.model.ResetPasswordRequest
import com.odevlibertario.satspass.model.SignUpRequest
import com.odevlibertario.satspass.model.UpdatePasswordRequest
import com.odevlibertario.satspass.model.User
import com.odevlibertario.satspass.model.UserRole
import com.odevlibertario.satspass.model.UserStatus
import com.odevlibertario.satspass.model.VerifyRequest
import com.odevlibertario.satspass.util.validateEmail
import com.odevlibertario.satspass.util.validateNotEmpty
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventService(
    val eventDao: EventDao
) {


}