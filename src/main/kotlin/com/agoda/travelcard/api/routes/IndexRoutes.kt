package com.agoda.travelcard.api.routes

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * GET /index — the liveness probe seen hitting the service every ~10s.
 * Intentionally cheap: no DB, no downstream calls.
 */
fun Application.indexRoutes() {
    routing {
        get("/index") {
            call.respondText("OK", ContentType.Text.Plain)
        }
    }
}
