package com.agoda.travelcard.common.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object TransactionsTable : LongIdTable("card_account_transactions") {
    val accountId = varchar("account_id", 64).index()
    val cardId = varchar("card_id", 64).nullable()
    val whitelabel = varchar("whitelabel", 32)
    val type = varchar("type", 32)
    val status = varchar("status", 32)
    val amountMinor = long("amount_minor")
    val currency = varchar("currency", 3)
    val merchantName = varchar("merchant_name", 128).nullable()
    val wiseReference = varchar("wise_reference", 64).nullable()
    val createdAt = timestamp("created_at")
}

object WalletsTable : LongIdTable("wallets") {
    val memberId = varchar("member_id", 64).uniqueIndex()
    val walletId = varchar("wallet_id", 64).uniqueIndex()
    val currency = varchar("currency", 3)
    val createdAt = timestamp("created_at")
}

object TravelCardsTable : LongIdTable("travel_cards") {
    val accountId = varchar("account_id", 64).index()
    val whitelabel = varchar("whitelabel", 32)
    val cardType = varchar("card_type", 16)
    val currency = varchar("currency", 3)
    val status = varchar("status", 16)
    val balanceMinor = long("balance_minor")
    val maskedPan = varchar("masked_pan", 32)
    val createdAt = timestamp("created_at")
}

object BookingsTable : LongIdTable("bookings") {
    val bookingRef = varchar("booking_ref", 32).uniqueIndex()
    val accountId = varchar("account_id", 64).index()
    val whitelabel = varchar("whitelabel", 32)
    val bookingType = varchar("booking_type", 16)
    val status = varchar("status", 16)
    val totalAmountMinor = long("total_amount_minor")
    val currency = varchar("currency", 3)
    val hotelName = varchar("hotel_name", 256).nullable()
    val destination = varchar("destination", 256).nullable()
    val checkIn = date("check_in").nullable()
    val checkOut = date("check_out").nullable()
    val rooms = integer("rooms").nullable()
    val guests = integer("guests").nullable()
    val airline = varchar("airline", 128).nullable()
    val flightNumber = varchar("flight_number", 32).nullable()
    val origin = varchar("origin", 8).nullable()
    val flightDestination = varchar("flight_destination", 8).nullable()
    val departureDate = date("departure_date").nullable()
    val returnDate = date("return_date").nullable()
    val passengers = integer("passengers").nullable()
    val cabinClass = varchar("cabin_class", 16).nullable()
    val createdAt = timestamp("created_at")
}

object PaymentsTable : LongIdTable("payments") {
    val paymentRef = varchar("payment_ref", 32).uniqueIndex()
    val accountId = varchar("account_id", 64).index()
    val whitelabel = varchar("whitelabel", 32)
    val cardId = long("card_id").nullable()
    val bookingRef = varchar("booking_ref", 32).nullable()
    val amountMinor = long("amount_minor")
    val currency = varchar("currency", 3)
    val paymentMethod = varchar("payment_method", 16)
    val status = varchar("status", 16)
    val createdAt = timestamp("created_at")
}

object ReviewsTable : LongIdTable("reviews") {
    val accountId = varchar("account_id", 64).index()
    val whitelabel = varchar("whitelabel", 32)
    val bookingRef = varchar("booking_ref", 32)
    val propertyName = varchar("property_name", 256)
    val rating = integer("rating")
    val title = varchar("title", 256)
    val comment = text("comment")
    val travelType = varchar("travel_type", 16)
    val createdAt = timestamp("created_at")
}
