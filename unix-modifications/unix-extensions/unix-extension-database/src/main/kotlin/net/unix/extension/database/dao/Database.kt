@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.extension.database.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.unix.extension.database.DatabaseExtension
import net.unix.node.CloudExtension
import net.unix.extension.database.dao.connection.ConnectionInfo
import net.unix.extension.database.dao.connection.ConnectionType
import net.unix.extension.database.dao.mysql.GroupManagerMySQL
import net.unix.extension.database.dao.mysql.ServiceManagerMySQL
import net.unix.extension.database.dao.mysql.TemplateManagerMySQL
import net.unix.extension.database.dao.sqlite.GroupManagerSQLite
import net.unix.extension.database.dao.sqlite.ServiceManagerSQLite
import net.unix.extension.database.dao.sqlite.TemplateManagerSQLite
import org.jdbi.v3.cache.caffeine.CaffeineCachePlugin
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.HandleConsumer
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.async.JdbiExecutor
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object Database : KoinComponent {

    lateinit var executor: JdbiExecutor

    fun install(connectionInfo: ConnectionInfo) {

        if(!connectionInfo.use) return

        when(connectionInfo.type) {
            ConnectionType.MySQL -> {
                executor = createMySQLDatabase(
                    connectionInfo.host,
                    connectionInfo.port,
                    connectionInfo.database,
                    connectionInfo.user,
                    connectionInfo.password
                )
                GroupManagerDao.clazz = GroupManagerMySQL::class.java
                TemplateManagerDao.clazz = TemplateManagerMySQL::class.java
                ServiceManagerDao.clazz = ServiceManagerMySQL::class.java
            }
            ConnectionType.SQLite -> {
                executor = createSQLiteDatabase()

                GroupManagerDao.clazz = GroupManagerSQLite::class.java
                TemplateManagerDao.clazz = TemplateManagerSQLite::class.java
                ServiceManagerDao.clazz = ServiceManagerSQLite::class.java
            }
        }
    }

    private fun createSQLiteDatabase(fileName: String = "database.db", templateFileName: String = "template.sql"): JdbiExecutor {
        val db = File(DatabaseExtension.instance.folder, fileName)

        if (!db.exists()) {
            try {
                db.createNewFile()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        val config = HikariConfig()
        config.driverClassName = "org.sqlite.JDBC"
        config.connectionTestQuery = "SELECT 1"
        config.jdbcUrl = "jdbc:sqlite:$db"

        val dataSource = HikariDataSource(config)

        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(CaffeineCachePlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        try {
            CloudExtension::class.java.getResourceAsStream("/$templateFileName").use { inputStream ->
                jdbi.useHandle(
                    HandleConsumer<IOException> { handle: Handle ->
                        handle.createScript(
                            String(
                                inputStream!!.readBytes()
                            )
                        ).execute()
                    })
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        val executorObj: Executor = Executors.newFixedThreadPool(8)

        return JdbiExecutor.create(jdbi, executorObj)
    }

    private fun createMySQLDatabase(host: String, port: Int, database: String, user: String, password: String, templateFileName: String = "template.sql"): JdbiExecutor {
        val config = HikariConfig()

        config.jdbcUrl = "jdbc:mysql://" + host +
                ":" + port +
                "/" + database + "?autoReconnect=true&useSSL=false&characterEncoding=utf8"
        config.username = user
        config.password = password

        val dataSource = HikariDataSource(config)

        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(CaffeineCachePlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        try {
            CloudExtension::class.java.getResourceAsStream("/$templateFileName").use { inputStream ->
                jdbi.useHandle(HandleConsumer<IOException> { handle: Handle ->
                    handle.createScript(
                        String(inputStream!!.readAllBytes())
                    ).execute()
                })
                jdbi.useHandle(HandleConsumer<RuntimeException> { handle: Handle ->
                    handle.createUpdate(
                        "SET @@group_concat_max_len = 1000000"
                    ).execute()
                })
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        val executorObj = Executors.newFixedThreadPool(8)

        return JdbiExecutor.create(jdbi, executorObj)
    }

}