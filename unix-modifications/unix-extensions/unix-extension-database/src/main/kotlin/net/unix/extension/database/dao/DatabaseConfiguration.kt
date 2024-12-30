package net.unix.extension.database.dao

import net.unix.extension.database.DatabaseExtension
import net.unix.node.configuration.CloudConfiguration
import net.unix.extension.database.dao.connection.ConnectionInfo
import net.unix.extension.database.dao.connection.ConnectionType

@Suppress("MemberVisibilityCanBePrivate")
object DatabaseConfiguration : CloudConfiguration("extensions/${DatabaseExtension.instance.info.name}/database.json") {

    val useDatabase: Boolean = values["use"].toString().toBoolean()
    val type: ConnectionType = ConnectionType.valueOf(values["type"].toString())

    val host: String = values["host"].toString()
    val port: Int = values["port"].toString().toDouble().toInt()
    val database: String = values["database"].toString()
    val user: String = values["user"].toString()
    val password: String = values["password"].toString()

    val connectionInfo: ConnectionInfo
        get() = ConnectionInfo(useDatabase, type, host, port, database, user, password)

    override fun default(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["use"] = false
        serialized["type"] = "SQLite"

        serialized["host"] = "localhost"
        serialized["port"] = 3306
        serialized["database"] = "db"
        serialized["user"] = "root"
        serialized["password"] = "root"

        return serialized
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["use"] = useDatabase
        serialized["type"] = type.name

        serialized["host"] = host
        serialized["port"] = port
        serialized["database"] = database
        serialized["user"] = user
        serialized["password"] = password

        return serialized
    }

}