package com.agoda.travelcard.booking

import com.agoda.travelcard.common.database.BookingsTable
import com.agoda.travelcard.common.database.DatabaseConnectionModule
import com.agoda.travelcard.common.errors.BadRequestException
import com.agoda.travelcard.common.errors.NotFoundException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import kotlin.random.Random

data class BookingRecord(
    val bookingRef: String,
    val accountId: String,
    val whitelabel: String,
    val bookingType: String,
    val status: String,
    val totalAmountMinor: Long,
    val currency: String,
    val hotelName: String?,
    val destination: String?,
    val checkIn: String?,
    val checkOut: String?,
    val rooms: Int?,
    val guests: Int?,
    val airline: String?,
    val flightNumber: String?,
    val origin: String?,
    val flightDestination: String?,
    val departureDate: String?,
    val returnDate: String?,
    val passengers: Int?,
    val cabinClass: String?,
    val createdAtEpochMs: Long,
)

data class HotelBookingInput(
    val accountId: String,
    val whitelabel: String,
    val hotelName: String,
    val destination: String,
    val checkIn: String,
    val checkOut: String,
    val rooms: Int,
    val guests: Int,
    val totalAmountMinor: Long,
    val currency: String,
)

data class FlightBookingInput(
    val accountId: String,
    val whitelabel: String,
    val airline: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureDate: String,
    val returnDate: String?,
    val passengers: Int,
    val cabinClass: String,
    val totalAmountMinor: Long,
    val currency: String,
)

class BookingService {
    fun bookHotel(input: HotelBookingInput): BookingRecord {
        if (input.accountId.isBlank()) throw BadRequestException("accountId is required")
        if (input.totalAmountMinor <= 0) throw BadRequestException("totalAmountMinor must be > 0")
        val ref = "HTL-" + randomRef()
        return transaction(DatabaseConnectionModule.database()) {
            BookingsTable.insertAndGetId {
                it[bookingRef] = ref
                it[accountId] = input.accountId
                it[whitelabel] = input.whitelabel
                it[bookingType] = "HOTEL"
                it[status] = "CONFIRMED"
                it[totalAmountMinor] = input.totalAmountMinor
                it[currency] = input.currency.uppercase()
                it[hotelName] = input.hotelName
                it[destination] = input.destination
                it[checkIn] = LocalDate.parse(input.checkIn)
                it[checkOut] = LocalDate.parse(input.checkOut)
                it[rooms] = input.rooms
                it[guests] = input.guests
                it[createdAt] = Instant.now()
            }
            load(ref)
        }
    }

    fun bookFlight(input: FlightBookingInput): BookingRecord {
        if (input.accountId.isBlank()) throw BadRequestException("accountId is required")
        if (input.totalAmountMinor <= 0) throw BadRequestException("totalAmountMinor must be > 0")
        val cabin = input.cabinClass.uppercase()
        if (cabin !in setOf("ECONOMY", "PREMIUM_ECONOMY", "BUSINESS", "FIRST")) {
            throw BadRequestException("cabinClass must be ECONOMY/PREMIUM_ECONOMY/BUSINESS/FIRST")
        }
        val ref = "FLT-" + randomRef()
        return transaction(DatabaseConnectionModule.database()) {
            BookingsTable.insertAndGetId {
                it[bookingRef] = ref
                it[accountId] = input.accountId
                it[whitelabel] = input.whitelabel
                it[bookingType] = "FLIGHT"
                it[status] = "CONFIRMED"
                it[totalAmountMinor] = input.totalAmountMinor
                it[currency] = input.currency.uppercase()
                it[airline] = input.airline
                it[flightNumber] = input.flightNumber
                it[origin] = input.origin.uppercase()
                it[flightDestination] = input.destination.uppercase()
                it[departureDate] = LocalDate.parse(input.departureDate)
                it[returnDate] = input.returnDate?.let(LocalDate::parse)
                it[passengers] = input.passengers
                it[cabinClass] = cabin
                it[createdAt] = Instant.now()
            }
            load(ref)
        }
    }

    fun cancel(bookingRef: String): BookingRecord {
        if (bookingRef.isBlank()) throw BadRequestException("bookingRef is required")
        return transaction(DatabaseConnectionModule.database()) {
            loadRow(bookingRef)
            BookingsTable.update({ BookingsTable.bookingRef eq bookingRef }) {
                it[status] = "CANCELLED"
            }
            load(bookingRef)
        }
    }

    private fun load(bookingRef: String): BookingRecord = toRecord(loadRow(bookingRef))

    private fun loadRow(bookingRef: String): ResultRow =
        BookingsTable.select { BookingsTable.bookingRef eq bookingRef }.limit(1).firstOrNull()
            ?: throw NotFoundException("Booking $bookingRef not found")

    private fun toRecord(row: ResultRow) = BookingRecord(
        bookingRef = row[BookingsTable.bookingRef],
        accountId = row[BookingsTable.accountId],
        whitelabel = row[BookingsTable.whitelabel],
        bookingType = row[BookingsTable.bookingType],
        status = row[BookingsTable.status],
        totalAmountMinor = row[BookingsTable.totalAmountMinor],
        currency = row[BookingsTable.currency],
        hotelName = row[BookingsTable.hotelName],
        destination = row[BookingsTable.destination],
        checkIn = row[BookingsTable.checkIn]?.toString(),
        checkOut = row[BookingsTable.checkOut]?.toString(),
        rooms = row[BookingsTable.rooms],
        guests = row[BookingsTable.guests],
        airline = row[BookingsTable.airline],
        flightNumber = row[BookingsTable.flightNumber],
        origin = row[BookingsTable.origin],
        flightDestination = row[BookingsTable.flightDestination],
        departureDate = row[BookingsTable.departureDate]?.toString(),
        returnDate = row[BookingsTable.returnDate]?.toString(),
        passengers = row[BookingsTable.passengers],
        cabinClass = row[BookingsTable.cabinClass],
        createdAtEpochMs = row[BookingsTable.createdAt].toEpochMilli(),
    )

    private fun randomRef(): String =
        (1..8).map { REF_ALPHABET[Random.nextInt(REF_ALPHABET.length)] }.joinToString("")

    companion object {
        private const val REF_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    }
}
