package net.unix.extension.database.template

import net.unix.api.template.CloudFile
import net.unix.api.template.CloudTemplate
import net.unix.api.template.SaveableCloudTemplate
import net.unix.api.template.SaveableCloudTemplateManager
import net.unix.node.CloudExtension.readJson
import net.unix.extension.database.dao.Database
import net.unix.extension.database.dao.TemplateManagerDao
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.template.TemplateCreateEvent
import net.unix.node.logging.CloudLogger
import net.unix.node.node.ThisNode
import net.unix.node.template.BasicCloudTemplate
import java.io.File
import java.util.concurrent.CompletableFuture

object DatabaseBasicTemplateManager : SaveableCloudTemplateManager {

    private fun readResolve(): Any = DatabaseBasicTemplateManager

    override val templates: Set<CloudTemplate>
        get() {
            val completable = CompletableFuture<Set<CloudTemplate>>()

            Database.executor.withExtension<_, _, Exception>(TemplateManagerDao.clazz) { dao ->
                return@withExtension dao.templatesOfNode(ThisNode.name)
            }
                .thenAccept {
                    completable.complete(it.mapNotNull { it.toCloudTemplate() }.toSet())
                }
                .exceptionally { ex ->
                    CloudLogger.exception(ex)
                    null
                }

            return completable.join()
        }

    override fun newInstance(name: String, files: MutableList<CloudFile>, backFiles: MutableList<CloudFile>): CloudTemplate {
        val template = BasicCloudTemplate(
            name,
            files = files,
            backFiles = backFiles
        )

        TemplateCreateEvent(template).callEvent()

        register(template)

        return template
    }

    override fun delete(template: CloudTemplate) {
        unregister(template)
        template.delete()
    }

    override fun register(template: CloudTemplate) {
        Database.executor.useExtension<_, Exception>(TemplateManagerDao.clazz) { dao ->
            dao.create(
                ThisNode.name,
                template.name,
                template.persistentDataContainer,
                template.files,
                template.backFiles
            )
        }
            .exceptionally { ex ->
                CloudLogger.exception(ex)
                null
            }

        CloudLogger.debug("Registered template ${template.name}")
    }

    override fun unregister(template: CloudTemplate) {
        Database.executor.useExtension<_, Exception>(TemplateManagerDao.clazz) { dao ->
            dao.delete(ThisNode.name, template.name)
        }
            .exceptionally { ex ->
                CloudLogger.exception(ex)
                null
            }
    }

    override fun get(name: String): CloudTemplate? {
        val completable = CompletableFuture<CloudTemplate?>()

        Database.executor.withExtension<_, _, Exception>(TemplateManagerDao.clazz) { dao ->
            return@withExtension dao[ThisNode.name, name]
        }
            .thenAccept {
                completable.complete(it?.toCloudTemplate())
            }
            .exceptionally { ex ->
                CloudLogger.exception(ex)
                null
            }

        return completable.join()
    }

    override fun loadAllTemplates() {
        val templates = templates

        CloudLogger.info("Loaded ${templates.size} templates:")
        templates.forEach {
            CloudLogger.info(" - ${it.name}")
        }
    }

    override fun loadTemplate(file: File): SaveableCloudTemplate {
        val template = BasicCloudTemplate.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded template ${template.name}")

        register(template)

        return template
    }
}