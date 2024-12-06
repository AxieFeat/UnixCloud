package net.unix.module.rest.defaultcontroller.template

import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateManager
import net.unix.module.rest.annotation.*
import net.unix.module.rest.controller.Controller
import net.unix.node.template.BasicCloudTemplate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("unused")
@RestController("cloud/template/")
class TemplateController : Controller, KoinComponent {

    private val cloudTemplateManager: CloudTemplateManager by inject()

    @RequestMapping(RequestType.GET, "", "web.cloud.template.get.all")
    fun handleGetAllTemplates(): Set<CloudTemplate> {
        return cloudTemplateManager.templates
    }

    @RequestMapping(RequestType.GET, "name/:name/", "web.cloud.template.get.one")
    fun handleGetOneTemplates(@RequestPathParam("name") name: String): CloudTemplate {
        return cloudTemplateManager[name] ?: throwNoSuchElement()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.template.create")
    fun handleCreateTemplate(@RequestBody template: BasicCloudTemplate): CloudTemplate {
        if (doesTemplateExist(template.name)) throwElementAlreadyExist()

        cloudTemplateManager.register(template)

        return template
    }

    @RequestMapping(RequestType.DELETE, "name/:name", "web.cloud.template.delete")
    fun handleDeleteTemplate(@RequestPathParam("name") name: String): CloudTemplate {
        if (!doesTemplateExist(name)) throwNoSuchElement()
        val template = cloudTemplateManager[name]!!
        cloudTemplateManager.delete(template)
        return template
    }

    private fun doesTemplateExist(name: String): Boolean {
        return cloudTemplateManager.templates.any { it.name == name }
    }


}