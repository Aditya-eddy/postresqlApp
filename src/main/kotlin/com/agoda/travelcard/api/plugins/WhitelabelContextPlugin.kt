package com.agoda.travelcard.api.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.request.header
import io.ktor.util.AttributeKey

/**
 * Whitelabel branding/segmentation context — mirrors Agoda's WhitelabelContextPlugin.
 *
 * Reads the X-Agoda-Whitelabel header (defaulting to "AGODA") and attaches a
 * [WhitelabelContext] to the call so downstream handlers can brand/segment their
 * responses for partner-branded experiences.
 */
data class WhitelabelContext(val whitelabelId: String, val locale: String?)

val WhitelabelContextKey: AttributeKey<WhitelabelContext> = AttributeKey("WhitelabelContext")

val ApplicationCall.whitelabel: WhitelabelContext
    get() = attributes.getOrNull(WhitelabelContextKey) ?: WhitelabelContext("AGODA", null)

private val WhitelabelPlugin = createApplicationPlugin("WhitelabelContext") {
    onCall { call ->
        val wl = call.request.header("X-Agoda-Whitelabel") ?: "AGODA"
        val locale = call.request.header("X-Agoda-Locale")
        call.attributes.put(WhitelabelContextKey, WhitelabelContext(wl.uppercase(), locale))
    }
}

fun Application.configureWhitelabelContext() {
    install(WhitelabelPlugin)
}
