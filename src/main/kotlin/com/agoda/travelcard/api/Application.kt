package com.agoda.travelcard.api

import com.agoda.travelcard.api.plugins.configureCallLogging
import com.agoda.travelcard.api.plugins.configureContentNegotiation
import com.agoda.travelcard.api.plugins.configureHealthCheck
import com.agoda.travelcard.api.plugins.configureOpenApi
import com.agoda.travelcard.api.plugins.configureStatusPages
import com.agoda.travelcard.api.plugins.configureWhitelabelContext
import com.agoda.travelcard.api.routes.accountRoutes
import com.agoda.travelcard.api.routes.bookingRoutes
import com.agoda.travelcard.api.routes.cardRoutes
import com.agoda.travelcard.api.routes.indexRoutes
import com.agoda.travelcard.api.routes.paymentRoutes
import com.agoda.travelcard.api.routes.reviewRoutes
import com.agoda.travelcard.common.config.AppConfig
import com.agoda.travelcard.common.database.DatabaseConnectionModule
import com.agoda.travelcard.common.di.appModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.agoda.travelcard.api.Application")

fun main() {
    val config = AppConfig.load()
    logger.info(
        "Starting {} on {}:{} (cluster={})",
        config.service.name,
        config.deployment.host,
        config.deployment.port,
        config.service.cluster,
    )

    embeddedServer(
        Netty,
        port = config.deployment.port,
        host = config.deployment.host,
    ) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val config = AppConfig.load()

    install(Koin) {
        slf4jLogger()
        modules(appModule(config))
    }

    // Bootstrap DB pool — mirrors the real service which fails here if Postgres is unreachable.
    DatabaseConnectionModule.initialize(config.database)

    configureCallLogging()
    configureContentNegotiation()
    configureStatusPages()
    configureOpenApi()
    configureHealthCheck()
    configureWhitelabelContext()

    indexRoutes()
    accountRoutes()
    cardRoutes()
    bookingRoutes()
    paymentRoutes()
    reviewRoutes()

    logger.info("travel-card-api-standalone ready")
}
