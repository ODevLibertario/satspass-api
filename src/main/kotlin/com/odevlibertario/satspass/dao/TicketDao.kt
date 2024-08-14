package com.odevlibertario.satspass.dao

import com.odevlibertario.satspass.model.Currency
import com.odevlibertario.satspass.model.TicketCategory
import com.odevlibertario.satspass.model.UpsertTicketCategoryRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant

@Repository
class TicketDao(val jdbcTemplate: JdbcTemplate) {
    fun addTicketCategory(ticketCategory: TicketCategory) {
        jdbcTemplate.update("""
            INSERT INTO satspass.ticket_category (
                id,
                event_id,
                category_name,
                price,
                currency,
                quantity,
                sales_start_date,
                sales_end_date
            )VALUES(
                ?::UUID,
                ?::UUID,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?
            ) 
            
        """.trimIndent()
        ) { ps ->
            ps.setString(1, ticketCategory.id)
            ps.setString(2, ticketCategory.eventId)
            ps.setString(3, ticketCategory.categoryName)
            ps.setInt(4, ticketCategory.price)
            ps.setObject(5, ticketCategory.currency.name)
            ps.setInt(6, ticketCategory.quantity)
            ps.setObject(7, Timestamp.from(ticketCategory.salesStarDate), Types.TIMESTAMP)
            ps.setObject(8, Timestamp.from(ticketCategory.salesEndDate), Types.TIMESTAMP)        }
    }

    fun getTicketCategories(eventId: String): List<TicketCategory> {
        return jdbcTemplate.query("""
            SELECT id,
                event_id,
                category_name,
                price,
                currency,
                quantity,
                sales_start_date,
                sales_end_date,
                created_at,
                updated_at
                FROM satspass.ticket_category
                WHERE event_id = ?::uuid  
        """.trimIndent(), ticketCategoryRomMapper(), eventId)
    }

    private fun ticketCategoryRomMapper() = { rs: ResultSet, _: Int ->
        TicketCategory(
            rs.getString("id"),
            rs.getString("event_id"),
            rs.getString("category_name"),
            rs.getInt("price"),
            Currency.valueOf(rs.getString("currency")),
            rs.getInt("quantity"),
            rs.getTimestamp("sales_start_date").toInstant(),
            rs.getTimestamp("sales_end_date").toInstant(),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
        )


    }
    
    fun updateTicketCategory(eventId: String, ticketCategoryId: String, request: UpsertTicketCategoryRequest) {
        jdbcTemplate.update("""
            UPDATE satspass.ticket_category
            SET category_name = ?,
                price = ?,
                currency = ?,
                quantity = ?,
                sales_start_date = ?,
                sales_end_date = ?,
                updated_at = ?
            WHERE id = ?::uuid
        """.trimIndent()
        ){ ps ->
            ps.setString(1, request.categoryName)
            ps.setInt(2, request.price)
            ps.setString(3, request.currency.name)
            ps.setInt(4, request.quantity)
            ps.setObject(5, Timestamp.from(request.salesStartDate), Types.TIMESTAMP)
            ps.setObject(6, Timestamp.from(request.salesEndDate), Types.TIMESTAMP)
            ps.setObject(7, Timestamp.from(Instant.now()), Types.TIMESTAMP)
            ps.setString(8, ticketCategoryId)
        }
    }
    fun deleteTicketCategory(ticketCategoryId: String) {
        jdbcTemplate.update("""
            DELETE FROM satspass.ticket_category
            WHERE id = ?::uuid
            """.trimIndent(), ticketCategoryId
        )
    }



}