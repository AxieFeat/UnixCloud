package net.unix.api.module.annotation

import net.unix.api.module.ModuleData

fun ModuleData.Companion.createByAnnotation(main: String, moduleInfo: ModuleInfo): ModuleData {
    return ModuleData(
        main,
        moduleInfo.name,
        moduleInfo.version,
        moduleInfo.description,
        moduleInfo.website,
        moduleInfo.authors.toList(),
        moduleInfo.depends.toList(),
        moduleInfo.softDepends.toList()
    )
}