package com.agoda.travelcard.common.database

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

data class TransactionRecord(
    val id: Long,
    val accountId: String,
    val cardId: String?,
    val whitelabel: String,
    val type: String,
    val status: String,
    val amountMinor: Long,
    val currency: String,
    val merchantName: String?,
    val wiseReference: String?,
    val createdAt: Instant,
)

class TransactionRepository {
    fun listByAccount(
        accountId: String,
        whitelabel: String,
        limit: Int,
        offset: Long,
    ): List<TransactionRecord> = transaction(DatabaseConnectionModule.database()) {
        TransactionsTable
            .select {
                (TransactionsTable.accountId eq accountId) and
                    (TransactionsTable.whitelabel eq whitelabel)
            }
            .orderBy(TransactionsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row ->
                TransactionRecord(
                    id = row[TransactionsTable.id].value,
                    accountId = row[TransactionsTable.accountId],
                    cardId = row[TransactionsTable.cardId],
                    whitelabel = row[TransactionsTable.whitelabel],
                    type = row[TransactionsTable.type],
                    status = row[TransactionsTable.status],
                    amountMinor = row[TransactionsTable.amountMinor],
                    currency = row[TransactionsTable.currency],
                    merchantName = row[TransactionsTable.merchantName],
                    wiseReference = row[TransactionsTable.wiseReference],
                    createdAt = row[TransactionsTable.createdAt],
                )
            }
    }
}
