package com.agoda.travelcard.review

import com.agoda.travelcard.common.database.DatabaseConnectionModule
import com.agoda.travelcard.common.database.ReviewsTable
import com.agoda.travelcard.common.errors.BadRequestException
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

data class ReviewRecord(
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

data class SubmitReviewInput(
    val accountId: String,
    val whitelabel: String,
    val bookingRef: String,
    val propertyName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val travelType: String,
)

class ReviewService {
    fun submit(input: SubmitReviewInput): ReviewRecord {
        if (input.accountId.isBlank()) throw BadRequestException("accountId is required")
        if (input.bookingRef.isBlank()) throw BadRequestException("bookingRef is required")
        if (input.rating !in 1..10) throw BadRequestException("rating must be in 1..10")
        val travelType = input.travelType.uppercase()
        if (travelType !in setOf("SOLO", "COUPLE", "FAMILY", "BUSINESS")) {
            throw BadRequestException("travelType must be SOLO/COUPLE/FAMILY/BUSINESS")
        }
        return transaction(DatabaseConnectionModule.database()) {
            val insertedId = ReviewsTable.insertAndGetId {
                it[accountId] = input.accountId
                it[whitelabel] = input.whitelabel
                it[bookingRef] = input.bookingRef
                it[propertyName] = input.propertyName
                it[rating] = input.rating
                it[title] = input.title
                it[comment] = input.comment
                it[ReviewsTable.travelType] = travelType
                it[createdAt] = Instant.now()
            }.value
            ReviewsTable.select { ReviewsTable.id eq insertedId }.first().let { row ->
                ReviewRecord(
                    id = row[ReviewsTable.id].value,
                    accountId = row[ReviewsTable.accountId],
                    whitelabel = row[ReviewsTable.whitelabel],
                    bookingRef = row[ReviewsTable.bookingRef],
                    propertyName = row[ReviewsTable.propertyName],
                    rating = row[ReviewsTable.rating],
                    title = row[ReviewsTable.title],
                    comment = row[ReviewsTable.comment],
                    travelType = row[ReviewsTable.travelType],
                    createdAtEpochMs = row[ReviewsTable.createdAt].toEpochMilli(),
                )
            }
        }
    }
}
