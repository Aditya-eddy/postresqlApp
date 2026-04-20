package com.agoda.travelcard.card

import com.agoda.travelcard.common.database.DatabaseConnectionModule
import com.agoda.travelcard.common.database.TravelCardsTable
import com.agoda.travelcard.common.errors.BadRequestException
import com.agoda.travelcard.common.errors.NotFoundException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import kotlin.random.Random

data class CardRecord(
    val id: Long,
    val accountId: String,
    val whitelabel: String,
    val cardType: String,
    val currency: String,
    val status: String,
    val balanceMinor: Long,
    val maskedPan: String,
    val createdAtEpochMs: Long,
)

class CardService {
    fun issue(accountId: String, whitelabel: String, cardType: String, currency: String): CardRecord {
        if (accountId.isBlank()) throw BadRequestException("accountId is required")
        val normalisedType = cardType.uppercase()
        if (normalisedType !in setOf("VIRTUAL", "PHYSICAL")) {
            throw BadRequestException("cardType must be VIRTUAL or PHYSICAL")
        }
        if (currency.length != 3) throw BadRequestException("currency must be ISO 4217 (3 letters)")

        val pan = "**** **** **** " + Random.nextInt(1000, 9999).toString()
        return transaction(DatabaseConnectionModule.database()) {
            val insertedId = TravelCardsTable.insertAndGetId {
                it[TravelCardsTable.accountId] = accountId
                it[TravelCardsTable.whitelabel] = whitelabel
                it[TravelCardsTable.cardType] = normalisedType
                it[TravelCardsTable.currency] = currency.uppercase()
                it[TravelCardsTable.status] = "ACTIVE"
                it[TravelCardsTable.balanceMinor] = 0
                it[TravelCardsTable.maskedPan] = pan
                it[TravelCardsTable.createdAt] = Instant.now()
            }.value
            load(insertedId)
        }
    }

    fun topUp(cardId: Long, amountMinor: Long): CardRecord {
        if (amountMinor <= 0) throw BadRequestException("amountMinor must be > 0")
        return transaction(DatabaseConnectionModule.database()) {
            val current = loadRow(cardId)
            TravelCardsTable.update({ TravelCardsTable.id eq cardId }) {
                it[balanceMinor] = current[TravelCardsTable.balanceMinor] + amountMinor
            }
            load(cardId)
        }
    }

    fun toggleFreeze(cardId: Long): CardRecord {
        return transaction(DatabaseConnectionModule.database()) {
            val current = loadRow(cardId)
            val next = if (current[TravelCardsTable.status] == "FROZEN") "ACTIVE" else "FROZEN"
            TravelCardsTable.update({ TravelCardsTable.id eq cardId }) {
                it[status] = next
            }
            load(cardId)
        }
    }

    private fun load(cardId: Long): CardRecord = toRecord(loadRow(cardId))

    private fun loadRow(cardId: Long): ResultRow =
        TravelCardsTable.select { TravelCardsTable.id eq cardId }.limit(1).firstOrNull()
            ?: throw NotFoundException("Card $cardId not found")

    private fun toRecord(row: ResultRow) = CardRecord(
        id = row[TravelCardsTable.id].value,
        accountId = row[TravelCardsTable.accountId],
        whitelabel = row[TravelCardsTable.whitelabel],
        cardType = row[TravelCardsTable.cardType],
        currency = row[TravelCardsTable.currency],
        status = row[TravelCardsTable.status],
        balanceMinor = row[TravelCardsTable.balanceMinor],
        maskedPan = row[TravelCardsTable.maskedPan],
        createdAtEpochMs = row[TravelCardsTable.createdAt].toEpochMilli(),
    )
}
