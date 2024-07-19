package net.unix.api.modification.module.annotation

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
    }
}