package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupBuilder
import net.unix.api.template.CloudTemplate

class CloudGroupBuilderImpl : CloudGroupBuilder {
    override fun name(name: String): CloudGroupBuilder {
        TODO("Not yet implemented")
    }

    override fun static(boolean: Boolean): CloudGroupBuilder {
        TODO("Not yet implemented")
    }

    override fun host(host: String): CloudGroupBuilder {
        TODO("Not yet implemented")
    }

    override fun ports(vararg ports: Int): CloudGroupBuilder {
        TODO("Not yet implemented")
    }

    override fun withTemplates(vararg templates: CloudTemplate): CloudGroupBuilder {
        TODO("Not yet implemented")
    }

    override fun build(): CloudGroup {
        TODO("Not yet implemented")
    }
}