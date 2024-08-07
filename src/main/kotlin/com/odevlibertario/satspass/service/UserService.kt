package com.odevlibertario.satspass.service

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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    val passwordEncoder: PasswordEncoder,
    val userDao: UserDao,
    val emailService: EmailService
) {

    @Transactional(rollbackFor = [Exception::class])
    fun signUp(request: SignUpRequest) {
        request.email.validateNotEmpty().validateEmail()
        request.password.validateNotEmpty()

        val id = UUID.randomUUID().toString()

        userDao.addUser(
            User(
                id,
                request.email,
                request.username,
                passwordEncoder.encode(request.password),
                UserStatus.PENDING_EMAIL_CONFIRMATION
            )
        )
        userDao.addRole(id, UserRole.EVENT_CUSTOMER)
        emailService.sendSimpleEmail(request.email, "Satspass - Verificação de email", "Seu código para a verificação é: ${id.takeLast(4)}")
    }

    fun verifyEmail(request: VerifyRequest) {
        val userId = userDao.getUser(request.email)?.id
        val verified = userId != null && userId.toString().takeLast(4) == request.code
        if(verified) {
            userDao.updateUserStatus(userId!!, UserStatus.ACTIVE)
        } else {
            throw IllegalArgumentException("Failed to verify email")
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun resetPassword(request: ResetPasswordRequest) {
        val newPassword = UUID.randomUUID().toString().replace("-", "").takeLast(6)

        userDao.updateUserPassword(request.email, passwordEncoder.encode(newPassword))
        emailService.sendSimpleEmail(request.email, "Satspass - Recuperação de Senha", "Seu nova senha para a plataforma é: $newPassword")
    }

    fun updatePassword(request: UpdatePasswordRequest) {
        val user = userDao.getUser(request.email) ?: throw IllegalArgumentException("User not found")

        if(user.password != passwordEncoder.encode(request.oldPassword)) {
            throw IllegalArgumentException("Incorrect current password")
        }

        userDao.updateUserPassword(request.email, passwordEncoder.encode(request.newPassword))
    }


}