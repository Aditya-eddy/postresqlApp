package com.agoda.travelcard.common.database

import com.agoda.travelcard.common.config.DatabaseConfig
import com.agoda.travelcard.common.errors.DatabaseNotInitializedException
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/**
 * Bootstraps the HikariCP connection pool named "agoda-wallet-postgres\pgsql"
 * and exposes it to the rest of the app. In Keploy-replayed environments this
 * typically fails because Postgres isn't reachable — resulting in the 500s
 * observed on POST /account/transactions.
 */
object DatabaseConnectionModule {
    private val logger = LoggerFactory.getLogger(DatabaseConnectionModule::class.java)

    @Volatile private var dataSource: HikariDataSource? = null
    @Volatile private var database: Database? = null

    fun initialize(cfg: DatabaseConfig) {
        if (dataSource != null) return
        synchronized(this) {
            if (dataSource != null) return
            try {
                // Use the no-arg HikariDataSource() constructor so the pool is
                // created lazily on the first getConnection() call (i.e. on the
                // first DB query), instead of eagerly at app startup as the
                // HikariDataSource(HikariConfig) constructor would do.
                val ds = HikariDataSource().apply {
                    poolName = cfg.poolName
                    jdbcUrl = cfg.jdbcUrl
                    username = cfg.username
                    password = cfg.password
                    driverClassName = "org.postgresql.Driver"
                    maximumPoolSize = cfg.maximumPoolSize
                    connectionTimeout = cfg.connectionTimeoutMs
                    isAutoCommit = false
                }
                dataSource = ds
                database = Database.connect(ds)
                logger.info("HikariCP pool '{}' configured (lazy) against {}", cfg.poolName, cfg.jdbcUrl)
            } catch (t: Throwable) {
                logger.error(
                    "Failed to initialize HikariCP pool '{}' against {}: {}",
                    cfg.poolName,
                    cfg.jdbcUrl,
                    t.message,
                )
                // Don't rethrow — match real service behaviour where the server still
                // boots and returns 500s on DB-backed endpoints.
            }
        }
    }

    fun isHealthy(): Boolean {
        val ds = dataSource ?: return false
        return try {
            ds.connection.use { it.isValid(1) }
        } catch (t: Throwable) {
            logger.warn("DB health check failed: {}", t.message)
            false
        }
    }

    fun database(): Database =
        database ?: throw DatabaseNotInitializedException()

    fun dataSource(): DataSource =
        dataSource ?: throw DatabaseNotInitializedException()
}
