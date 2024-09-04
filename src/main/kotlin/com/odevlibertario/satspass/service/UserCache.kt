package com.odevlibertario.satspass.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.odevlibertario.satspass.dao.UserDao
import com.odevlibertario.satspass.model.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class UserCache (
   private val userDao: UserDao
){
    private val cache: Cache<String, Pair<User, List<String>>> = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build()

    fun invalidate(email: String){
        cache.invalidate(email)
    }

    fun getUserAndRoles(email: String): Pair<User, List<String>>{
        return cache.get(email, {key ->
            val user = userDao.getUser(key) ?: throw UsernameNotFoundException("User not found")
            val roles = userDao.getRoles(user.id)
            Pair(user, roles)
        } )!!
    }
}
