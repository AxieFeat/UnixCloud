package net.unix.cloud.service

import net.unix.api.module.CloudModule
import net.unix.api.module.annotation.ModuleInfo

@ModuleInfo(
    name = "Examle", // <- Название модуля, обязательное значение
    version = "1.0", // <- Версия модуля, обязательное значение
    authors = ["AxieFeat"], // <- Авторы модуля
    website = "example.org", // <- Сайт модуля
    description = "Example module", // <- Описание модуля
    depends = [ // <- Обязательные зависимости модуля
        "UnixCore"
    ],
    softDepends = [ // <- Опциональные зависимости модуля
        "UnixReplacer"
    ]
)
class CloudModuleExample : CloudModule() {

    override fun onLoad() {
        // Выполняется при загрузке модуля
    }

    override fun onReload() {
        // Выполняется при перезагрузке модуля
    }

    override fun onUnload() {
        // Выполняется при выгрузке модуля
    }
}


