package net.unix.cloud

import net.unix.api.event.impl.cloud.CloudShutdownEvent

object CloudInstanceShutdownHandler {
    fun run() {
        CloudShutdownEvent().callEvent()
        CloudInstance.terminal.close()
    }
}