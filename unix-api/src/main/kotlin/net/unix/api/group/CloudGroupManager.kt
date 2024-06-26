package net.unix.api.group

import net.unix.api.template.CloudTemplate

interface CloudGroupManager {

    fun createGroup(name: String, static: Boolean, host: String, availablePorts: MutableList<Int>, startupCount: Int, templates: MutableList<CloudTemplate>): CloudGroup
}