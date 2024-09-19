package com.odevlibertario.satspass.dao

import com.odevlibertario.satspass.model.User
import com.odevlibertario.satspass.model.UserRole
import com.odevlibertario.satspass.model.UserStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class UserDao(val jdbcTemplate: JdbcTemplate) {
    fun addUser(user: User) {
        jdbcTemplate.update("""
            INSERT INTO satspass.user (
                id,
                email,
                username,
                password_hash,
                status
            ) VALUES (
                ?::uuid,
                ?,
                ?,
                ?,
                ?::satspass.user_status
            )
        """.trimIndent(),
            user.id.toString(),
            user.email,
            user.username,
            user.password,
            user.userStatus.name
            )



    }

    fun addRole(userId: String, userRole: UserRole) {
        jdbcTemplate.update("""
            INSERT INTO satspass.user_role (
                user_id,
                role
            ) VALUES (
                ?::uuid,
                ?::satspass.role
            )
        """.trimIndent(),
            userId,
            userRole.name
        )
    }

    fun getUser(email: String): User? {
        return jdbcTemplate.query("SELECT id, email, username, password_hash, status, lightning_address, created_at, updated_at FROM satspass.user WHERE email = ?",
            userRowMapper(), email).firstOrNull()
    }

    fun getUserById(userId: String): User? {
        return jdbcTemplate.query("SELECT id, email, username, password_hash, status, lightning_address, created_at, updated_at FROM satspass.user WHERE id = ?::uuid",
            userRowMapper(), userId).firstOrNull()
    }

    private fun userRowMapper() = { rs: ResultSet, _: Int ->
        User(
            rs.getString("id"),
            rs.getString("email"),
            rs.getString("username"),
            rs.getString("password_hash"),
            UserStatus.valueOf(rs.getString("status")),
            rs.getString("lightning_address"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant(),
        )
    }


    fun updateUserStatus(id: String, userStatus: UserStatus) {
        jdbcTemplate.update("UPDATE satspass.user SET status = ?::satspass.user_status, updated_at = now() WHERE id = ?::uuid", userStatus.name, id)
    }

    fun updateUserPassword(email: String, passwordHash: String) {
        jdbcTemplate.update("UPDATE satspass.user SET password_hash = ?, updated_at = now() WHERE email = ?", passwordHash, email)
    }

    fun getRoles(userId: String): List<String> {
        return jdbcTemplate.query("SELECT role FROM satspass.user_role WHERE user_id = ?::uuid",
            { rs: ResultSet, _: Int ->
                rs.getString("role")
            }, userId)

    }

    fun updateLightningAddress(userId: String, lightningAddress: String) {
        jdbcTemplate.update("UPDATE satspass.user SET lightning_address = ?, updated_at = now() WHERE id = ?::uuid", lightningAddress, userId)
    }
}