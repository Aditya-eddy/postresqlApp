package com.agoda.travelcard.common.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

data class DeploymentConfig(val host: String, val port: Int)

data class ServiceConfig(val name: String, val cluster: String)

data class DatabaseConfig(
    val poolName: String,
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int,
    val connectionTimeoutMs: Long,
)

data class VaultConfig(val address: String, val token: String)

data class ProxyConfig(val host: String, val port: Int, val enabled: Boolean)

data class WiseSubsidyConfig(val clientId: String, val clientSecret: String)

data class WiseConfig(
    val baseUrl: String,
    val clientId: String,
    val clientSecret: String,
    val physicalCardProgram: String,
    val virtualCardProgram: String,
    val subsidy: WiseSubsidyConfig,
)

data class ObservabilityConfig(val otlpEndpoint: String, val serviceName: String)

data class AppConfig(
    val deployment: DeploymentConfig,
    val service: ServiceConfig,
    val database: DatabaseConfig,
    val vault: VaultConfig,
    val proxy: ProxyConfig,
    val wise: WiseConfig,
    val observability: ObservabilityConfig,
) {
    companion object {
        @Volatile private var cached: AppConfig? = null

        fun load(): AppConfig {
            cached?.let { return it }
            synchronized(this) {
                cached?.let { return it }
                val raw: Config = ConfigFactory.load()
                val appConfig = AppConfig(
                    deployment = DeploymentConfig(
                        host = raw.getString("ktor.deployment.host"),
                        port = raw.getInt("ktor.deployment.port"),
                    ),
                    service = ServiceConfig(
                        name = raw.getString("service.name"),
                        cluster = raw.getString("service.cluster"),
                    ),
                    database = DatabaseConfig(
                        poolName = raw.getString("database.poolName"),
                        jdbcUrl = raw.getString("database.jdbcUrl"),
                        username = raw.getString("database.username"),
                        password = raw.getString("database.password"),
                        maximumPoolSize = raw.getInt("database.maximumPoolSize"),
                        connectionTimeoutMs = raw.getLong("database.connectionTimeoutMs"),
                    ),
                    vault = VaultConfig(
                        address = raw.getString("vault.address"),
                        token = raw.getString("vault.token"),
                    ),
                    proxy = ProxyConfig(
                        host = raw.getString("proxy.host"),
                        port = raw.getInt("proxy.port"),
                        enabled = raw.getBoolean("proxy.enabled"),
                    ),
                    wise = WiseConfig(
                        baseUrl = raw.getString("wise.baseUrl"),
                        clientId = raw.getString("wise.clientId"),
                        clientSecret = raw.getString("wise.clientSecret"),
                        physicalCardProgram = raw.getString("wise.physicalCardProgram"),
                        virtualCardProgram = raw.getString("wise.virtualCardProgram"),
                        subsidy = WiseSubsidyConfig(
                            clientId = raw.getString("wise.subsidy.clientId"),
                            clientSecret = raw.getString("wise.subsidy.clientSecret"),
                        ),
                    ),
                    observability = ObservabilityConfig(
                        otlpEndpoint = raw.getString("observability.otlpEndpoint"),
                        serviceName = raw.getString("observability.serviceName"),
                    ),
                )
                cached = appConfig
                return appConfig
            }
        }
    }
}
