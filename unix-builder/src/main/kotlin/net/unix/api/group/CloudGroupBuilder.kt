package net.unix.api.group

import net.unix.api.builder.Builder
import net.unix.api.template.CloudTemplate

interface CloudGroupBuilder : Builder<CloudGroup> {

    fun name(name: String): CloudGroupBuilder
    fun static(boolean: Boolean): CloudGroupBuilder
    fun host(host: String): CloudGroupBuilder
    fun ports(vararg ports: Int): CloudGroupBuilder
    fun withTemplates(vararg templates: CloudTemplate):CloudGroupBuilder

    override fun build(): CloudGroup {
        TODO("Not yet implemented")
    }
}