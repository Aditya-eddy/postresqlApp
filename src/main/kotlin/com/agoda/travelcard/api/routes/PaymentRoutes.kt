package com.agoda.travelcard.api.routes

import com.agoda.travelcard.api.plugins.whitelabel
import com.agoda.travelcard.payment.PaymentRecord
import com.agoda.travelcard.payment.PaymentService
import com.agoda.travelcard.payment.ProcessPaymentInput
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
data class ProcessPaymentRequest(
    val accountId: String,
    val cardId: Long? = null,
    val bookingRef: String? = null,
    val amountMinor: Long,
    val currency: String,
    val paymentMethod: String,
)

@Serializable
data class RefundRequest(val paymentRef: String)

@Serializable
data class PaymentDto(
    val paymentRef: String,
    val accountId: String,
    val whitelabel: String,
    val cardId: Long? = null,
    val bookingRef: String? = null,
    val amountMinor: Long,
    val currency: String,
    val paymentMethod: String,
    val status: String,
    val createdAtEpochMs: Long,
)

private fun PaymentRecord.toDto() = PaymentDto(
    paymentRef, accountId, whitelabel, cardId, bookingRef, amountMinor, currency,
    paymentMethod, status, createdAtEpochMs,
)

fun Application.paymentRoutes() {
    val service: PaymentService by inject()
    routing {
        route("/payment") {
            post("/process") {
                val req = call.receive<ProcessPaymentRequest>()
                val wl = call.whitelabel.whitelabelId
                val payment = service.process(
                    ProcessPaymentInput(
                        accountId = req.accountId,
                        whitelabel = wl,
                        cardId = req.cardId,
                        bookingRef = req.bookingRef,
                        amountMinor = req.amountMinor,
                        currency = req.currency,
                        paymentMethod = req.paymentMethod,
                    ),
                )
                call.respond(HttpStatusCode.Created, payment.toDto())
            }
            post("/refund") {
                val req = call.receive<RefundRequest>()
                call.respond(service.refund(req.paymentRef).toDto())
            }
        }
    }
}
