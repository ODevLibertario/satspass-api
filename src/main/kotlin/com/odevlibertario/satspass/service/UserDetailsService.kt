package com.odevlibertario.satspass.service

import com.odevlibertario.satspass.dao.UserDao
import com.odevlibertario.satspass.model.SatspassUserDetails
import com.odevlibertario.satspass.model.UserStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService(
    val userCache: UserCache
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val userAndRoles = userCache.getUserAndRoles(email)
        val user = userAndRoles.first
        val roles = userAndRoles.second

        return SatspassUserDetails(user.id, user.email, user.password, roles.map { role -> SimpleGrantedAuthority(role) }.toMutableList())
    }
}