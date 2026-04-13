package com.agoda.travelcard.api.plugins

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Serves a minimal OpenAPI document. In the real Agoda service this comes from
 * com.agoda.bootstrap.core.plugin (AutoDoc) — here we hand-roll the doc to stay
 * free of proprietary dependencies.
 */
fun Application.configureOpenApi() {
    routing {
        get("/openapi.json") {
            call.respondText(OPENAPI_DOC, ContentType.Application.Json)
        }
        get("/docs") {
            call.respondText(SWAGGER_UI_HTML, ContentType.Text.Html)
        }
    }
}

private val OPENAPI_DOC = """
{
  "openapi": "3.0.3",
  "info": {
    "title": "travel-card-api-standalone",
    "version": "0.1.0",
    "description": "Issues and manages travel debit cards for Agoda customers via Wise."
  },
  "servers": [{ "url": "http://localhost:8080" }],
  "paths": {
    "/index": {
      "get": {
        "summary": "Liveness probe",
        "responses": { "200": { "description": "OK" } }
      }
    },
    "/healthcheck": {
      "get": {
        "summary": "Readiness check (DB + Wise)",
        "responses": {
          "200": { "description": "Healthy" },
          "503": { "description": "Unhealthy" }
        }
      }
    },
    "/account/transactions": {
      "post": {
        "summary": "List/page transactions for a travel-card account",
        "responses": {
          "200": { "description": "OK" },
          "400": { "description": "Bad Request" },
          "500": { "description": "Internal Error" }
        }
      }
    }
  }
}
""".trimIndent()

private val SWAGGER_UI_HTML = """
<!doctype html>
<html><head><title>travel-card-api-standalone</title></head>
<body><h1>travel-card-api-standalone</h1>
<p>See <a href="/openapi.json">/openapi.json</a> for the spec.</p></body></html>
""".trimIndent()
