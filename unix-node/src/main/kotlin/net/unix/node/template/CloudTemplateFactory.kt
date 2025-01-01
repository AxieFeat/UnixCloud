package net.unix.node.template

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.api.template.Template
import net.unix.api.template.TemplateFactory
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.template.TemplateCreateEvent

object CloudTemplateFactory : TemplateFactory {

    private fun readResolve(): Any = CloudTemplateFactory

    override fun create(
        name: String,
        persistent: PersistentDataContainer,
        files: MutableList<CloudFile>,
        backFiles: MutableList<CloudFile>
    ): Template {
        val template = BasicTemplate(
            name,
            persistent,
            files,
            backFiles
        )

        TemplateCreateEvent(template).callEvent()

        return template
    }

}