package com.odevlibertario.satspass.dao

import com.odevlibertario.satspass.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant

@Repository
class TicketDao(val jdbcTemplate: JdbcTemplate) {
    fun addTicketCategory(ticketCategory: TicketCategory) {
        jdbcTemplate.update(
            """
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
            ps.setObject(7, Timestamp.from(ticketCategory.salesStartDate), Types.TIMESTAMP)
            ps.setObject(8, Timestamp.from(ticketCategory.salesEndDate), Types.TIMESTAMP)
        }
    }

    fun getTicketCategories(eventId: String): List<TicketCategory> {
        return jdbcTemplate.query(
            """
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
        """.trimIndent(), ticketCategoryRomMapper(), eventId
        )
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
        jdbcTemplate.update(
            """
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
        ) { ps ->
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
        jdbcTemplate.update(
            """
            DELETE FROM satspass.ticket_category
            WHERE id = ?::uuid
            """.trimIndent(), ticketCategoryId
        )
    }

    fun getCountForTickerCategory(tickerCategoryId: String): Int {
        return jdbcTemplate.query("""
            SELECT count(*) AS c FROM satspass.ticket
            WHERE ticket_category_id = ?::uuid           
        """, RowMapper { rs: ResultSet, _: Int -> rs.getInt("c") }, tickerCategoryId
        ).first()
    }

    fun getTicketCategory(ticketCategoryId: String): TicketCategory {
        return jdbcTemplate.query(
            """
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
                WHERE id = ?::uuid  
        """.trimIndent(), ticketCategoryRomMapper(), ticketCategoryId
        ).first()
    }

    fun addTicket(ticket: Ticket) {
        jdbcTemplate.update(
            """
            INSERT INTO satspass.ticket(
                id,
                event_id,
                ticket_category_id,
                user_id,
                qr_code,
                status,
                payment_hash,
                invoice
        ) VALUES (
            ?::uuid,
            ?::uuid,
            ?::uuid,
            ?::uuid,
            ?,
            ?:: satspass.ticket_status,
            ?,
            ?
            )
            """.trimIndent()
        ) { ps ->
            ps.setString(1, ticket.id)
            ps.setString(2, ticket.eventId)
            ps.setString(3, ticket.ticketCategoryId)
            ps.setString(4, ticket.userId)
            ps.setString(5, ticket.qrCode)
            ps.setString(6, ticket.ticketStatus.name)
            ps.setString(7, ticket.paymentHash)
            ps.setString(8, ticket.invoice)
        }
    }

    fun getTicketsAndEvent(userId: String): List<TicketAndEvent> {
        return jdbcTemplate.query(
            """
            select 
            t.id as ticket_id,
            c.category_name,
            t.qr_code, 
            t.invoice,
            t.status as ticket_status, 
            e.name, 
            e.start_date, 
            e.end_date, 
            e.start_time, 
            e.end_time, 
            e.description, 
            e.location, 
            e.publicity_image_url 
            from satspass.ticket t 
            inner join satspass.ticket_category c on t.ticket_category_id = c.id 
            inner join satspass.event e on t.event_id = e.id 
            where t.user_id = ?::uuid and t.status in ('RESERVED', 'PURCHASED')
                """.trimIndent(), ticketAndEventRowMapper(), userId
        )
    }

    fun getQrCodeInfo(qrCode: String): TicketAndEvent? {
        return jdbcTemplate.query(
            """
            select 
            t.id as ticket_id,
            c.category_name,
            t.qr_code, 
            t.invoice,
            t.status as ticket_status, 
            e.name, 
            e.start_date, 
            e.end_date, 
            e.start_time, 
            e.end_time, 
            e.description, 
            e.location, 
            e.publicity_image_url 
            from satspass.ticket t 
            inner join satspass.ticket_category c on t.ticket_category_id = c.id 
            inner join satspass.event e on t.event_id = e.id 
            where t.qr_code = ? and t.status = 'PURCHASED'
                """.trimIndent(), ticketAndEventRowMapper(), qrCode
        ).firstOrNull()
    }

    private fun ticketAndEventRowMapper() = { rs: ResultSet, _: Int ->
        TicketAndEvent(
            rs.getString("ticket_id"),
            rs.getString("category_name"),
            rs.getString("qr_code"),
            rs.getString("invoice"),
            TicketStatus.valueOf(rs.getString("ticket_status")),
            rs.getString("name"),
            rs.getTimestamp("start_date").toInstant(),
            rs.getTimestamp("end_date").toInstant(),
            rs.getTimestamp("start_time").toInstant(),
            rs.getTimestamp("end_time").toInstant(),
            rs.getString("description"),
            rs.getString("location"),
            rs.getString("publicity_image_url")
        )
    }

    private fun ticketRowMapper() = { rs: ResultSet, _: Int ->
        Ticket(
            rs.getString("id"),
            rs.getString("event_id"),
            rs.getString("ticket_category_id"),
            rs.getString("user_id"),
            rs.getString("qr_code"),
            TicketStatus.valueOf(rs.getString("status")),
            rs.getString("payment_hash"),
            rs.getString("invoice"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant(),
        )
    }

    fun getTicket(ticketId: String): Ticket? {
        return jdbcTemplate.query(
            """
            SELECT id,
                event_id,
                ticket_category_id,
                user_id,
                qr_code,
                status,
                payment_hash,
                invoice,
                created_at,
                updated_at
                FROM satspass.ticket
                WHERE id = ?::uuid
                """.trimIndent(), ticketRowMapper(), ticketId
        ).firstOrNull()
    }

    fun updateTicket(ticketId: String, status: TicketStatus) {
        jdbcTemplate.update("""
            UPDATE satspass.ticket
            SET status = ?::satspass.ticket_status
            WHERE id = ?::uuid
            """.trimIndent(), status.name, ticketId)
    }

    fun lockTable(tableName: String, lockMode: String){
        jdbcTemplate.execute("LOCK TABLE $tableName IN $lockMode MODE")
    }
}
