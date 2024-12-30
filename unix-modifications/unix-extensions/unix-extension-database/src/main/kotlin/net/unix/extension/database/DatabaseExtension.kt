package net.unix.extension.database

import net.unix.extension.database.dao.Database
import net.unix.extension.database.dao.DatabaseConfiguration
import net.unix.node.event.koin.KoinModuleRegisterEvent
import net.unix.node.event.listener
import net.unix.node.modification.extension.CloudExtension
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
                    //single<TemplateManager>(named("default")) { DatabaseBasicTemplateManager }
                    //single<GroupManager>(named("default")) { DatabaseJVMGroupManager }
                }
            )
        }

    }

}