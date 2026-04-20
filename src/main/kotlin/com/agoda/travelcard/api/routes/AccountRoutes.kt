package com.agoda.travelcard.api.routes

import com.agoda.travelcard.account.AccountTransactionService
import com.agoda.travelcard.api.plugins.whitelabel
import com.agoda.travelcard.common.database.TransactionRecord
import com.agoda.travelcard.wallet.WalletService
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class TransactionQuery(
    val accountId: String,
    val limit: Int = 50,
    val offset: Long = 0,
)

@Serializable
data class TransactionDto(
    val id: Long,
    val accountId: String,
    val cardId: String?,
    val type: String,
    val status: String,
    val amountMinor: Long,
    val currency: String,
    val merchantName: String?,
    val wiseReference: String?,
    val createdAtEpochMs: Long,
)

@Serializable
data class TransactionListResponse(
    val whitelabel: String,
    val transactions: List<TransactionDto>,
)

private fun TransactionRecord.toDto() = TransactionDto(
    id = id,
    accountId = accountId,
    cardId = cardId,
    type = type,
    status = status,
    amountMinor = amountMinor,
    currency = currency,
    merchantName = merchantName,
    wiseReference = wiseReference,
    createdAtEpochMs = createdAt.toEpochMilli(),
)

@Serializable
data class WalletCheckResponse(
    val walletId: String,
    val memberId: String,
    val currency: String,
)

fun Application.accountRoutes() {
    val service: AccountTransactionService by inject()
    val walletService: WalletService by inject()
    routing {
        route("/account") {
            post("/transactions") {
                val query = call.receive<TransactionQuery>()
                val wl = call.whitelabel.whitelabelId
                val records = service.list(query.accountId, wl, query.limit, query.offset)
                call.respond(
                    TransactionListResponse(
                        whitelabel = wl,
                        transactions = records.map(TransactionRecord::toDto),
                    ),
                )
            }
            get("/wallet/check") {
                val memberId = call.request.header("X-Agoda-Member-Id").orEmpty()
                val wallet = walletService.check(memberId)
                call.respond(
                    WalletCheckResponse(
                        walletId = wallet.walletId,
                        memberId = wallet.memberId,
                        currency = wallet.currency,
                    ),
                )
            }
        }
    }
}
