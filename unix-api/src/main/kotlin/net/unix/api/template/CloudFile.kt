package net.unix.api.template

import net.unix.api.pattern.Serializable
import java.nio.file.Path
import java.nio.file.Paths

/**
 * File copy param's for [CloudTemplate].
 */
data class CloudFile(
    /**
     * The file to be copied.
     */
    val from: Path,
    /**
     * Where the file should be copied to.
     */
    val to: Path
) : Serializable {

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["from"] = from.toString()
        serialized["to"] = to.toString()

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
            val from = Paths.get(serialized["from"].toString())
            val to = Paths.get(serialized["to"].toString())

            return CloudFile(from, to)
        }
    }
}