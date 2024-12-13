package net.unix.node.database.connection

data class ConnectionInfo(
    val use: Boolean,
    val type: ConnectionType,
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val password: String
)