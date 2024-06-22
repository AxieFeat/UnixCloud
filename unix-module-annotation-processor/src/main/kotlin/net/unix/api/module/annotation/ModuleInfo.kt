package net.unix.api.module.annotation

@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleInfo(
    val name: String,
    val version: String,
    val description: String = "Не указано",
    val website: String= "Не указано",
    val authors: Array<out String> = [],
    val depends: Array<out String> = [],
    val softDepends: Array<out String> = []
)