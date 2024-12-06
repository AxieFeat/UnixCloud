package net.unix.node.node

import net.unix.api.node.Node
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.terminal.unixStartTime
import net.unix.node.terminal.unixUptime

/**
 * This object represents "this" node.
 */
object ThisNode : Node {

    private fun readResolve(): Any = ThisNode

    override val name: String
        get() = UnixConfiguration.node.name

    override val startTime: Long
        get() = unixStartTime

    override val uptime: Long
        get() = unixUptime

    override val usageMemory: Long
        get() = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

    override val freeMemory: Long
        get() = maxMemory - usageMemory

    override val maxMemory: Long
        get() = Runtime.getRuntime().maxMemory()

}