package net.unix.extension.database

import net.unix.api.group.CloudGroupManager
import net.unix.api.template.CloudTemplateManager
import net.unix.extension.database.dao.Database
import net.unix.extension.database.dao.DatabaseConfiguration
import net.unix.extension.database.group.DatabaseJVMGroupManager
import net.unix.extension.database.template.DatabaseBasicTemplateManager
import net.unix.node.event.koin.KoinModuleRegisterEvent
import net.unix.node.event.listener
import net.unix.node.modification.extension.CloudExtension
import org.koin.core.qualifier.named
import org.koin.dsl.module

class DatabaseExtension : CloudExtension() {

    companion object {
        lateinit var instance: DatabaseExtension
    }

    override fun onLoad() {
        instance = this

        Database.install(DatabaseConfiguration.connectionInfo)

        listener<KoinModuleRegisterEvent> { e ->
            if(!DatabaseConfiguration.connectionInfo.use) return@listener

            e.modules.add(
                module {
                    single<CloudTemplateManager>(named("default")) { DatabaseBasicTemplateManager }
                    single<CloudGroupManager>(named("default")) { DatabaseJVMGroupManager }
                }
            )
        }

    }

}