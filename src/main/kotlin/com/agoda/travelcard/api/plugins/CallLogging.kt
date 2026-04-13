package com.agoda.travelcard.api.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureCallLogging() {
    install(CallLogging) {
        level = Level.INFO
        // Filter out noisy health/liveness pings on /index — they hit every ~10s
        filter { call -> call.request.path() != "/index" }
    }
}
