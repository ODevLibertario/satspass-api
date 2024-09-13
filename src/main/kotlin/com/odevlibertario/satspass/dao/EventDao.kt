package com.odevlibertario.satspass.dao

import com.odevlibertario.satspass.model.Event
import com.odevlibertario.satspass.model.EventStatus
import com.odevlibertario.satspass.model.UpsertEventRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types

@Repository
class EventDao(val jdbcTemplate: JdbcTemplate) {
    fun addEvent(event: Event) {
        jdbcTemplate.update("""
            INSERT INTO satspass.event (
                id,
                manager_id,
                name,
                start_date,
                end_date,
                start_time,
                end_time,
                description,
                location,
                publicity_image_url,
                status
            ) VALUES (
                ?::uuid,
                ?::uuid,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?::satspass.event_status
            )
        """.trimIndent()
        ) { ps ->
            ps.setString(1, event.id)
            ps.setString(2, event.managerId)
            ps.setString(3, event.name)
            ps.setObject(4, Timestamp.from(event.startDate), Types.TIMESTAMP)
            ps.setObject(5, Timestamp.from(event.endDate), Types.TIMESTAMP)
            ps.setObject(6, Timestamp.from(event.startTime), Types.TIMESTAMP)
            ps.setObject(7, Timestamp.from(event.endTime), Types.TIMESTAMP)
            ps.setString(8, event.description)
            ps.setString(9, event.location)
            ps.setString(10, event.publicityImageUrl)
            ps.setString(11, event.eventStatus.name)
        }
    }

    fun updateEvent(eventId: String, request: UpsertEventRequest) {
        jdbcTemplate.update("""
            UPDATE satspass.event 
            SET name = ?,
                start_date = ?,
                end_date = ?,
                description = ?,
                location = ?,
                publicity_image_url = ?
            WHERE id = ?::uuid
            
        """.trimIndent()
        ) { ps ->
            ps.setString(1, request.name)
            ps.setObject(2, Timestamp.from(request.startDate), Types.TIMESTAMP)
            ps.setObject(3, Timestamp.from(request.endDate), Types.TIMESTAMP)
            ps.setString(4, request.description)
            ps.setString(5, request.location.toString())
            ps.setString(6, request.publicityImageUrl)
            ps.setString(7, eventId)
        }

    }

    fun publishEvent(eventId: String) {
        jdbcTemplate.update("""
            UPDATE satspass.event 
            SET   status = ?::satspass.event_status
            WHERE id = ?::uuid
            
        """.trimIndent()
        ) { ps ->
            ps.setString(1, EventStatus.PUBLISHED.name)
            ps.setString(2, eventId)
        }
    }

    fun getEvents(managerId: String): List<Event> {
        return jdbcTemplate.query("""
             SELECT id, 
                manager_id, 
                name, 
                start_date, 
                end_date, 
                start_time, 
                end_time,
                description,
                location,
                publicity_image_url, 
                status,
                created_at,
                updated_at
                 FROM satspass.event
                 WHERE manager_id = ?::uuid
        """.trimIndent(),eventRowMapper(),managerId)
    }
    private fun eventRowMapper() = { rs: ResultSet, _: Int ->
        Event(
            rs.getString("id"),
            rs.getString("manager_id"),
            rs.getString("name"),
            rs.getTimestamp("start_date").toInstant(),
            rs.getTimestamp("end_date").toInstant(),
            rs.getTimestamp("start_time").toInstant(),
            rs.getTimestamp("end_time").toInstant(),
            rs.getString("description"),
            rs.getString("location"),
            rs.getString("publicity_image_url"),
            EventStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant(),
        )
    }
    fun getEvent(eventId: String): Event? {
        return jdbcTemplate.query("""
             SELECT id, 
                manager_id, 
                name, 
                start_date, 
                end_date, 
                start_time,
                end_time,
                description,
                location,
                publicity_image_url, 
                status,
                created_at,
                updated_at
                 FROM satspass.event
                 WHERE id = ?::uuid
        """.trimIndent(),eventRowMapper(), eventId).firstOrNull()
    }
    fun deleteEvent(eventId: String) {
        jdbcTemplate.update("""
            DELETE FROM satspass.event
            WHERE id = ?::uuid
          """.trimIndent(), eventId
        )
    }

    fun getPublishedEvents(): List<Event> {
        return jdbcTemplate.query("""
            SELECT id, 
                manager_id, 
                name, 
                start_date, 
                end_date,
                start_time, 
                end_time,
                description,
                location,
                publicity_image_url, 
                status,
                created_at,
                updated_at
                FROM satspass.event
                Where status = ?:: satspass.event_status 
            """.trimIndent(), eventRowMapper(), EventStatus.PUBLISHED.name
        )
    }


}