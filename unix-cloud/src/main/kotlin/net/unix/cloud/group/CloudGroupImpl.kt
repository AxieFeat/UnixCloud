package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.cloud.template.CloudTemplateImpl

class CloudGroupImpl : CloudGroup, CloudTemplateImpl() {
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override val static: Boolean
        get() = TODO("Not yet implemented")
    override val host: String
        get() = TODO("Not yet implemented")
    override val availablePorts: MutableList<Int>
        get() = TODO("Not yet implemented")
}