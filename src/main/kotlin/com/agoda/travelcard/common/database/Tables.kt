package com.agoda.travelcard.common.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

/**
 * Transactions on a travel-card account — auth, capture, refund, fee, etc.
 */
object TransactionsTable : LongIdTable("card_account_transactions") {
    val accountId = varchar("account_id", 64).index()
    val cardId = varchar("card_id", 64).nullable()
    val whitelabel = varchar("whitelabel", 32)
    val type = varchar("type", 32) // AUTH, CAPTURE, REFUND, FEE
    val status = varchar("status", 32) // PENDING, COMPLETED, DECLINED
    val amountMinor = long("amount_minor") // in smallest currency unit
    val currency = varchar("currency", 3)
    val merchantName = varchar("merchant_name", 128).nullable()
    val wiseReference = varchar("wise_reference", 64).nullable()
    val createdAt = timestamp("created_at")
}
