package com.agoda.travelcard.wise

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.time.Instant

@Serializable
data class WiseTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("token_type") val tokenType: String = "Bearer",
)

/**
 * Handles OAuth client-credentials token acquisition and refresh for a single
 * Wise client. Tokens are cached until ~60s before expiry.
 */
class WiseTokenManager(
    private val httpClient: HttpClient,
    private val config: WiseClientConfig,
) {
    private val logger = LoggerFactory.getLogger("WiseTokenManager[${config.label}]")
    private val mutex = Mutex()
    @Volatile private var cached: String? = null
    @Volatile private var expiresAt: Instant = Instant.EPOCH

    suspend fun token(): String {
        val now = Instant.now()
        cached?.let { if (now.isBefore(expiresAt.minusSeconds(60))) return it }
        mutex.withLock {
            val again = cached
            if (again != null && Instant.now().isBefore(expiresAt.minusSeconds(60))) return again
            return refresh()
        }
    }

    private suspend fun refresh(): String {
        logger.info("Refreshing OAuth token for Wise client '{}'", config.label)
        val resp: WiseTokenResponse = httpClient.submitForm(
            url = "${config.baseUrl}/oauth/token",
            formParameters = Parameters.build {
                append("grant_type", "client_credentials")
                append("client_id", config.clientId)
                append("client_secret", config.clientSecret)
                if (config.program != null) append("scope", config.program)
            },
        ).body()
        cached = resp.accessToken
        expiresAt = Instant.now().plusSeconds(resp.expiresIn)
        return resp.accessToken
    }
}
