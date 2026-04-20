package com.agoda.travelcard.wallet

import com.agoda.travelcard.common.database.DatabaseConnectionModule
import com.agoda.travelcard.common.database.WalletsTable
import com.agoda.travelcard.common.errors.BadRequestException
import com.agoda.travelcard.common.errors.NotFoundException
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class WalletRecord(
    val walletId: String,
    val memberId: String,
    val currency: String,
)

class WalletService {
    fun check(memberId: String): WalletRecord {
        if (memberId.isBlank()) throw BadRequestException("X-Agoda-Member-Id header is required")
        return transaction(DatabaseConnectionModule.database()) {
            WalletsTable
                .select { WalletsTable.memberId eq memberId }
                .limit(1)
                .map {
                    WalletRecord(
                        walletId = it[WalletsTable.walletId],
                        memberId = it[WalletsTable.memberId],
                        currency = it[WalletsTable.currency],
                    )
                }
                .firstOrNull()
                ?: throw NotFoundException("No wallet for memberId=$memberId")
        }
    }
}
