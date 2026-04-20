package com.agoda.travelcard.api.routes

import com.agoda.travelcard.api.plugins.whitelabel
import com.agoda.travelcard.booking.BookingRecord
import com.agoda.travelcard.booking.BookingService
import com.agoda.travelcard.booking.FlightBookingInput
import com.agoda.travelcard.booking.HotelBookingInput
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class HotelBookingRequest(
    val accountId: String,
    val hotelName: String,
    val destination: String,
    val checkIn: String,
    val checkOut: String,
    val rooms: Int,
    val guests: Int,
    val totalAmountMinor: Long,
    val currency: String,
)

@Serializable
data class FlightBookingRequest(
    val accountId: String,
    val airline: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureDate: String,
    val returnDate: String? = null,
    val passengers: Int,
    val cabinClass: String,
    val totalAmountMinor: Long,
    val currency: String,
)

@Serializable
data class CancelBookingRequest(val bookingRef: String)

@Serializable
data class BookingDto(
    val bookingRef: String,
    val accountId: String,
    val whitelabel: String,
    val bookingType: String,
    val status: String,
    val totalAmountMinor: Long,
    val currency: String,
    val hotelName: String? = null,
    val destination: String? = null,
    val checkIn: String? = null,
    val checkOut: String? = null,
    val rooms: Int? = null,
    val guests: Int? = null,
    val airline: String? = null,
    val flightNumber: String? = null,
    val origin: String? = null,
    val flightDestination: String? = null,
    val departureDate: String? = null,
    val returnDate: String? = null,
    val passengers: Int? = null,
    val cabinClass: String? = null,
    val createdAtEpochMs: Long,
)

private fun BookingRecord.toDto() = BookingDto(
    bookingRef, accountId, whitelabel, bookingType, status, totalAmountMinor, currency,
    hotelName, destination, checkIn, checkOut, rooms, guests,
    airline, flightNumber, origin, flightDestination, departureDate, returnDate, passengers, cabinClass,
    createdAtEpochMs,
)

fun Application.bookingRoutes() {
    val service: BookingService by inject()
    routing {
        route("/booking") {
            post("/hotel") {
                val req = call.receive<HotelBookingRequest>()
                val wl = call.whitelabel.whitelabelId
                val booking = service.bookHotel(
                    HotelBookingInput(
                        accountId = req.accountId,
                        whitelabel = wl,
                        hotelName = req.hotelName,
                        destination = req.destination,
                        checkIn = req.checkIn,
                        checkOut = req.checkOut,
                        rooms = req.rooms,
                        guests = req.guests,
                        totalAmountMinor = req.totalAmountMinor,
                        currency = req.currency,
                    ),
                )
                call.respond(HttpStatusCode.Created, booking.toDto())
            }
            post("/flight") {
                val req = call.receive<FlightBookingRequest>()
                val wl = call.whitelabel.whitelabelId
                val booking = service.bookFlight(
                    FlightBookingInput(
                        accountId = req.accountId,
                        whitelabel = wl,
                        airline = req.airline,
                        flightNumber = req.flightNumber,
                        origin = req.origin,
                        destination = req.destination,
                        departureDate = req.departureDate,
                        returnDate = req.returnDate,
                        passengers = req.passengers,
                        cabinClass = req.cabinClass,
                        totalAmountMinor = req.totalAmountMinor,
                        currency = req.currency,
                    ),
                )
                call.respond(HttpStatusCode.Created, booking.toDto())
            }
            post("/cancel") {
                val req = call.receive<CancelBookingRequest>()
                call.respond(service.cancel(req.bookingRef).toDto())
            }
        }
    }
}
