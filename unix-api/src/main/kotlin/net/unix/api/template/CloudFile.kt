package net.unix.api.template

import net.unix.api.pattern.Serializable
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * File copy param's for [CloudTemplate].
 */
data class CloudFile(
    /**
     * The file to be copied.
     */
    @get:Throws(RemoteException::class)
    val from: String,
    /**
     * Where the file should be copied to.
     */
    @get:Throws(RemoteException::class)
    val to: String
) : Serializable, RemoteAccessible {


    @Throws(RemoteException::class)
    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["from"] = from
        serialized["to"] = to

        return serialized
    }

    companion object {

        /**
         * Deserialize [CloudFile] from [CloudFile.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [CloudFile].
         */
        fun deserialize(serialized: Map<String, Any>): CloudFile {
            val from = serialized["from"].toString()
            val to = serialized["to"].toString()

            return CloudFile(from, to)
        }
    }
}