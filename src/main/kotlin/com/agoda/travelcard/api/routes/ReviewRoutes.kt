package com.agoda.travelcard.api.routes

import com.agoda.travelcard.api.plugins.whitelabel
import com.agoda.travelcard.review.ReviewRecord
import com.agoda.travelcard.review.ReviewService
import com.agoda.travelcard.review.SubmitReviewInput
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
data class SubmitReviewRequest(
    val accountId: String,
    val bookingRef: String,
    val propertyName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val travelType: String,
)

@Serializable
data class ReviewDto(
    val id: Long,
    val accountId: String,
    val whitelabel: String,
    val bookingRef: String,
    val propertyName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val travelType: String,
    val createdAtEpochMs: Long,
)

private fun ReviewRecord.toDto() = ReviewDto(
    id, accountId, whitelabel, bookingRef, propertyName, rating, title, comment, travelType, createdAtEpochMs,
)

fun Application.reviewRoutes() {
    val service: ReviewService by inject()
    routing {
        route("/review") {
            post("/submit") {
                val req = call.receive<SubmitReviewRequest>()
                val wl = call.whitelabel.whitelabelId
                val review = service.submit(
                    SubmitReviewInput(
                        accountId = req.accountId,
                        whitelabel = wl,
                        bookingRef = req.bookingRef,
                        propertyName = req.propertyName,
                        rating = req.rating,
                        title = req.title,
                        comment = req.comment,
                        travelType = req.travelType,
                    ),
                )
                call.respond(HttpStatusCode.Created, review.toDto())
            }
        }
    }
}
