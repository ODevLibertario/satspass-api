package com.odevlibertario.satspass.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SatspassUserDetails(val id: String,
                          val usernameInternal:String,
                          val passwordInternal: String,
                          val authoritiesInternal: MutableCollection<out GrantedAuthority>): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authoritiesInternal
    }

    override fun getPassword(): String {
        return passwordInternal
    }

    override fun getUsername(): String {
        return usernameInternal
    }
}