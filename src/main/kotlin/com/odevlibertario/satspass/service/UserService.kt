package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.UserDao
import com.odevlibertario.satspass.model.*
import com.odevlibertario.satspass.util.validateEmail
import com.odevlibertario.satspass.util.validateNotEmpty
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException.BadRequest
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserService(
   private val passwordEncoder: PasswordEncoder,
   private val userDao: UserDao,
   private val emailService: EmailService,
   private val userCache: UserCache
) {

    @Transactional(rollbackFor = [Exception::class])
    fun signUp(request: SignUpRequest) {
        try {
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
        } catch (e: DuplicateKeyException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe")
        }
    }

    fun verifyEmail(request: VerifyRequest) {
        val userId = userDao.getUser(request.email)?.id
        val verified = userId != null && userId.toString().takeLast(4) == request.code
        if(verified) {
            userDao.updateUserStatus(userId!!, UserStatus.ACTIVE)
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to verify email")
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun resetPassword(request: ResetPasswordRequest) {
        val newPassword = UUID.randomUUID().toString().replace("-", "").takeLast(6)

        userDao.updateUserPassword(request.email, passwordEncoder.encode(newPassword))
        emailService.sendSimpleEmail(request.email, "Satspass - Recuperação de Senha", "Seu nova senha para a plataforma é: $newPassword")
    }

    fun updatePassword(request: UpdatePasswordRequest) {
        val user = userDao.getUser(request.email) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")

        if(user.password != passwordEncoder.encode(request.oldPassword)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect current password")
        }

        userDao.updateUserPassword(request.email, passwordEncoder.encode(request.newPassword))
    }

    fun grantRole(request: GrantRoleRequest){
        if(request.role == UserRole.ADMIN){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido adicionar o role ADMIN")
        }
        userDao.addRole(request.userId, request.role)
        userDao.getUserById(request.userId)?.let { userCache.invalidate(it.email) }
    }
}