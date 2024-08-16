package net.unix.cloud

import net.unix.api.event.impl.cloud.CloudShutdownEvent

class CloudInstanceShutdownHandler(
    private val instance: CloudInstance
) {
    fun run() {
        CloudShutdownEvent().callEvent()
        instance.server.close()
        instance.terminal.close()
    }
}