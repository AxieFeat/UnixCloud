package net.unix.api.modification

import net.unix.api.modification.extension.annotation.ExtensionInfo
import net.unix.api.modification.module.annotation.ModuleInfo

fun net.unix.api.modification.module.ModuleInfo.Companion.createByAnnotation(main: String, moduleInfo: ModuleInfo): net.unix.api.modification.module.ModuleInfo {
    return object : net.unix.api.modification.module.ModuleInfo {
        override val main: String = main
        override val name: String = moduleInfo.name
        override val version: String = moduleInfo.version
        override val description: String = moduleInfo.description
        override val website: String = moduleInfo.website
        override val authors: List<String> = moduleInfo.authors.toList()
        override val depends: List<String> = moduleInfo.depends.toList()
        override val soft: List<String> = moduleInfo.softDepends.toList()

        override fun serialize(): Map<String, Any> {
            val serialized = mutableMapOf<String, Any>()

            serialized["main"] = main
            serialized["name"] = name
            serialized["version"] = version
            serialized["description"] = description
            serialized["website"] = website
            serialized["authors"] = authors

            serialized["depends"] = depends
            serialized["soft"] = soft

            return serialized
        }
    }
}

fun net.unix.api.modification.extension.ExtensionInfo.Companion.createByAnnotation(main: String, extensionInfo: ExtensionInfo): net.unix.api.modification.extension.ExtensionInfo {
    return object : net.unix.api.modification.extension.ExtensionInfo {
        override val main: String = main
        override val name: String = extensionInfo.name
        override val version: String = extensionInfo.version
        override val description: String = extensionInfo.description
        override val website: String = extensionInfo.website
        override val authors: List<String> = extensionInfo.authors.toList()

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
}