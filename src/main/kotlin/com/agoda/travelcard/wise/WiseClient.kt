package com.agoda.travelcard.wise

import com.agoda.travelcard.common.errors.WiseApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

@Serializable
data class IssueCardRequest(
    val customerId: String,
    val currency: String,
    val program: String,
)

@Serializable
data class IssueCardResponse(
    val cardId: String,
    val status: String,
    val last4: String,
    val program: String,
)

/**
 * Thin wrapper over the Wise REST API, scoped to a particular program
 * (physical / virtual / subsidy). Each instance has its own token manager.
 */
class WiseClient(
    private val httpClient: HttpClient,
    private val config: WiseClientConfig,
    private val tokenManager: WiseTokenManager,
) {
    private val logger = LoggerFactory.getLogger("WiseClient[${config.label}]")

    suspend fun issueCard(customerId: String, currency: String): IssueCardResponse {
        val program = config.program
            ?: throw WiseApiException("Client '${config.label}' is not wired to a card program")
        val token = tokenManager.token()
        val resp: HttpResponse = httpClient.post("${config.baseUrl}/v1/cards") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(IssueCardRequest(customerId, currency, program))
        }
        if (resp.status != HttpStatusCode.OK && resp.status != HttpStatusCode.Created) {
            logger.warn("Wise responded {} for issueCard", resp.status)
            throw WiseApiException("Wise issueCard failed with ${resp.status}")
        }
        return resp.body()
    }
}
