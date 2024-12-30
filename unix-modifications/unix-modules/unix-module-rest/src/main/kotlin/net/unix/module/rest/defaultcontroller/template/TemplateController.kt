package net.unix.module.rest.defaultcontroller.template

import net.unix.api.template.Template
import net.unix.api.template.TemplateManager
import net.unix.module.rest.annotation.*
import net.unix.module.rest.controller.Controller
import net.unix.node.template.BasicTemplate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("unused")
@RestController("cloud/template/")
class TemplateController : Controller, KoinComponent {

    private val templateManager: TemplateManager by inject()

    @RequestMapping(RequestType.GET, "", "web.cloud.template.get.all")
    fun handleGetAllTemplates(): Set<Template> {
        return templateManager.templates
    }

    @RequestMapping(RequestType.GET, "name/:name/", "web.cloud.template.get.one")
    fun handleGetOneTemplates(@RequestPathParam("name") name: String): Template {
        return templateManager[name] ?: throwNoSuchElement()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.template.create")
    fun handleCreateTemplate(@RequestBody template: BasicTemplate): Template {
        if (doesTemplateExist(template.name)) throwElementAlreadyExist()

        templateManager.register(template)

        return template
    }

    @RequestMapping(RequestType.DELETE, "name/:name", "web.cloud.template.delete")
    fun handleDeleteTemplate(@RequestPathParam("name") name: String): Template {
        if (!doesTemplateExist(name)) throwNoSuchElement()
        val template = templateManager[name]!!
        templateManager.delete(template)
        return template
    }

    private fun doesTemplateExist(name: String): Boolean {
        return templateManager.templates.any { it.name == name }
    }


}