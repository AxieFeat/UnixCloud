package net.unix.api

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupBuilder
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceBuilder
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateBuilder

interface CloudBuilderExtension {
    fun CloudGroup.Companion.builder(): CloudGroupBuilder
    fun CloudService.Companion.builder(): CloudServiceBuilder
    fun CloudTemplate.Companion.builder(): CloudTemplateBuilder
}