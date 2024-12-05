package net.unix.node.modification.extension

import net.unix.api.modification.extension.ExtensionInfo

@Suppress("UNCHECKED_CAST")
data class CloudExtensionInfo(
    override val main: String,
    override val name: String,
    override val version: String,
    override val description: String,
    override val website: String,
    override val authors: List<String>
) : ExtensionInfo {
    
    companion object {

        /**
         * Deserialize [CloudExtensionInfo] from [CloudExtensionInfo.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [CloudExtensionInfo].
         */
        fun deserialize(serialized: Map<String, Any>): CloudExtensionInfo {

            val main = serialized["main"].toString()
            val name = serialized["name"].toString()
            val version = serialized["version"].toString()
            val description = serialized["description"].toString()
            val website = serialized["website"].toString()
            val authors = serialized["authors"] as List<String>?  ?: listOf()
            
            return CloudExtensionInfo(main, name, version, description, website, authors)
        }
    }
    
    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()
        
        serialized["main"] = main
        serialized["name"] = name
        serialized["version"] = version
        serialized["description"] = description
        serialized["website"] = website
        serialized["authors"] = authors
        
        return serialized
    }
    
}