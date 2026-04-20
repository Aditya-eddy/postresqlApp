package com.agoda.travelcard.api.routes

import com.agoda.travelcard.api.plugins.whitelabel
import com.agoda.travelcard.card.CardRecord
import com.agoda.travelcard.card.CardService
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
data class IssueCardRequest(val accountId: String, val cardType: String, val currency: String)

@Serializable
data class TopUpRequest(val cardId: Long, val amountMinor: Long)

@Serializable
data class FreezeRequest(val cardId: Long)

@Serializable
data class CardDto(
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

private fun CardRecord.toDto() = CardDto(
    id, accountId, whitelabel, cardType, currency, status, balanceMinor, maskedPan, createdAtEpochMs,
)

fun Application.cardRoutes() {
    val service: CardService by inject()
    routing {
        route("/card") {
            post("/issue") {
                val req = call.receive<IssueCardRequest>()
                val wl = call.whitelabel.whitelabelId
                val card = service.issue(req.accountId, wl, req.cardType, req.currency)
                call.respond(HttpStatusCode.Created, card.toDto())
            }
            post("/topup") {
                val req = call.receive<TopUpRequest>()
                call.respond(service.topUp(req.cardId, req.amountMinor).toDto())
            }
            post("/freeze") {
                val req = call.receive<FreezeRequest>()
                call.respond(service.toggleFreeze(req.cardId).toDto())
            }
        }
    }
}
