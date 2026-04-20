package com.agoda.travelcard.payment

import com.agoda.travelcard.common.database.DatabaseConnectionModule
import com.agoda.travelcard.common.database.PaymentsTable
import com.agoda.travelcard.common.errors.BadRequestException
import com.agoda.travelcard.common.errors.NotFoundException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import kotlin.random.Random

data class PaymentRecord(
    val paymentRef: String,
    val accountId: String,
    val whitelabel: String,
    val cardId: Long?,
    val bookingRef: String?,
    val amountMinor: Long,
    val currency: String,
    val paymentMethod: String,
    val status: String,
    val createdAtEpochMs: Long,
)

data class ProcessPaymentInput(
    val accountId: String,
    val whitelabel: String,
    val cardId: Long?,
    val bookingRef: String?,
    val amountMinor: Long,
    val currency: String,
    val paymentMethod: String,
)

class PaymentService {
    fun process(input: ProcessPaymentInput): PaymentRecord {
        if (input.accountId.isBlank()) throw BadRequestException("accountId is required")
        if (input.amountMinor <= 0) throw BadRequestException("amountMinor must be > 0")
        val method = input.paymentMethod.uppercase()
        if (method !in setOf("TRAVEL_CARD", "CREDIT_CARD")) {
            throw BadRequestException("paymentMethod must be TRAVEL_CARD or CREDIT_CARD")
        }
        val ref = "PAY-" + randomRef()
        return transaction(DatabaseConnectionModule.database()) {
            PaymentsTable.insertAndGetId {
                it[paymentRef] = ref
                it[accountId] = input.accountId
                it[whitelabel] = input.whitelabel
                it[cardId] = input.cardId
                it[bookingRef] = input.bookingRef
                it[amountMinor] = input.amountMinor
                it[currency] = input.currency.uppercase()
                it[paymentMethod] = method
                it[status] = "COMPLETED"
                it[createdAt] = Instant.now()
            }
            load(ref)
        }
    }

    fun refund(paymentRef: String): PaymentRecord {
        if (paymentRef.isBlank()) throw BadRequestException("paymentRef is required")
        return transaction(DatabaseConnectionModule.database()) {
            loadRow(paymentRef)
            PaymentsTable.update({ PaymentsTable.paymentRef eq paymentRef }) {
                it[status] = "REFUNDED"
            }
            load(paymentRef)
        }
    }

    private fun load(ref: String): PaymentRecord = toRecord(loadRow(ref))

    private fun loadRow(ref: String): ResultRow =
        PaymentsTable.select { PaymentsTable.paymentRef eq ref }.limit(1).firstOrNull()
            ?: throw NotFoundException("Payment $ref not found")

    private fun toRecord(row: ResultRow) = PaymentRecord(
        paymentRef = row[PaymentsTable.paymentRef],
        accountId = row[PaymentsTable.accountId],
        whitelabel = row[PaymentsTable.whitelabel],
        cardId = row[PaymentsTable.cardId],
        bookingRef = row[PaymentsTable.bookingRef],
        amountMinor = row[PaymentsTable.amountMinor],
        currency = row[PaymentsTable.currency],
        paymentMethod = row[PaymentsTable.paymentMethod],
        status = row[PaymentsTable.status],
        createdAtEpochMs = row[PaymentsTable.createdAt].toEpochMilli(),
    )

    private fun randomRef(): String =
        (1..8).map { REF_ALPHABET[Random.nextInt(REF_ALPHABET.length)] }.joinToString("")

    companion object {
        private const val REF_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    }
}
