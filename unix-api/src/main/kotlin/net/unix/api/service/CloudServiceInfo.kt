package net.unix.api.service

import net.unix.api.pattern.Serializable
import java.util.UUID

/**
 * Info about service for unix-driver api.
 */
data class CloudServiceInfo(
    val name: String,
    val uuid: UUID
) : Serializable {

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["name"] = name
        serialized["uuid"] = uuid.toString()

        return serialized
    }

    companion object {
        fun deserialize(serialized: Map<String, Any>): CloudServiceInfo {

            val name = serialized["name"].toString()
            val uuid = UUID.fromString(serialized["uuid"].toString())

            return CloudServiceInfo(name, uuid)
        }
    }
}