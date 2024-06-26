package net.unix.api.group

import net.unix.api.template.CloudTemplate

interface CloudGroup {
    var name: String
    val static: Boolean
    val host: String
    val availablePorts: MutableList<Int>
    val startupCount: Int
    val templates: MutableList<CloudTemplate>

    companion object
}