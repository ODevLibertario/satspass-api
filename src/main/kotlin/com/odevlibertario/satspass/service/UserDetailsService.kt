package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.UserDao
import com.odevlibertario.satspass.model.SignUpRequest
import com.odevlibertario.satspass.model.User
import com.odevlibertario.satspass.model.UserRole
import com.odevlibertario.satspass.model.UserStatus
import com.odevlibertario.satspass.model.VerifyRequest
import com.odevlibertario.satspass.util.validateEmail
import com.odevlibertario.satspass.util.validateNotEmpty
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserDetailsService(
    val userDao: UserDao
) : UserDetailsService {

    //Add cache here
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userDao.getUser(email) ?: throw UsernameNotFoundException("User not found")

        val roles = userDao.getRoles(user.id)

        return org.springframework.security.core.userdetails.User.builder()
            .username(email)
            .password(user.password)
            .authorities(roles.map { role -> SimpleGrantedAuthority(role) })
            .disabled(user.userStatus != UserStatus.ACTIVE)
            .build()
    }
}