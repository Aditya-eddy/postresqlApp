package com.agoda.travelcard.common.di

import com.agoda.travelcard.account.AccountTransactionService
import com.agoda.travelcard.common.config.AppConfig
import com.agoda.travelcard.common.database.TransactionRepository
import com.agoda.travelcard.wise.WiseClient
import com.agoda.travelcard.wise.WiseClientConfig
import com.agoda.travelcard.wise.WiseClients
import com.agoda.travelcard.wise.WiseTokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModule(config: AppConfig): Module = module {
    single { config }

    single {
        HttpClient(CIO) {
            engine {
                if (config.proxy.enabled) {
                    proxy = ProxyBuilder.http(
                        URLBuilder()
                            .apply {
                                protocol = io.ktor.http.URLProtocol.HTTP
                                host = config.proxy.host
                                port = config.proxy.port
                            }
                            .build(),
                    )
                }
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
            }
            install(Logging)
        }
    }

    // Three Wise clients — physical, virtual, subsidy. Each has its own token.
    single {
        val http: HttpClient = get()
        val physicalCfg = WiseClientConfig(
            baseUrl = config.wise.baseUrl,
            clientId = config.wise.clientId,
            clientSecret = config.wise.clientSecret,
            program = config.wise.physicalCardProgram,
            label = "physical",
        )
        val virtualCfg = physicalCfg.copy(program = config.wise.virtualCardProgram, label = "virtual")
        val subsidyCfg = WiseClientConfig(
            baseUrl = config.wise.baseUrl,
            clientId = config.wise.subsidy.clientId,
            clientSecret = config.wise.subsidy.clientSecret,
            program = null,
            label = "subsidy",
        )
        WiseClients(
            physical = WiseClient(http, physicalCfg, WiseTokenManager(http, physicalCfg)),
            virtual = WiseClient(http, virtualCfg, WiseTokenManager(http, virtualCfg)),
            subsidy = WiseClient(http, subsidyCfg, WiseTokenManager(http, subsidyCfg)),
        )
    }

    single { TransactionRepository() }
    single { AccountTransactionService(get()) }
}
