package com.odevlibertario.satspass.util

import com.odevlibertario.satspass.model.SatspassUserDetails
import org.springframework.security.core.context.SecurityContextHolder

fun getCurrentUser(): SatspassUserDetails {
    return SecurityContextHolder.getContext().authentication.principal as SatspassUserDetails
}