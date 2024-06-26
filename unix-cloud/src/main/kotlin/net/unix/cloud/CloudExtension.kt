package net.unix.cloud

import net.unix.api.CloudExtension
import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupBuilder
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceBuilder
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateBuilder

object CloudExtension : CloudExtension {
    override fun CloudGroup.Companion.builder(): CloudGroupBuilder {
        TODO("Not yet implemented")
    }

    override fun CloudService.Companion.builder(): CloudServiceBuilder {
        TODO("Not yet implemented")
    }

    override fun CloudTemplate.Companion.builder(): CloudTemplateBuilder {
        TODO("Not yet implemented")
    }
}