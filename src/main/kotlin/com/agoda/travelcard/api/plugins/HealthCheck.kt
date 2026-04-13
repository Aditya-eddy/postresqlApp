package com.agoda.travelcard.api.plugins

import com.agoda.travelcard.common.database.DatabaseConnectionModule
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class HealthStatus(val service: String, val database: String, val healthy: Boolean)

fun Application.configureHealthCheck() {
    routing {
        get("/healthcheck") {
            val dbHealthy = DatabaseConnectionModule.isHealthy()
            val status = HealthStatus(
                service = "UP",
                database = if (dbHealthy) "UP" else "DOWN",
                healthy = dbHealthy,
            )
            val code = if (dbHealthy) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable
            call.respond(code, status)
        }
    }
}
