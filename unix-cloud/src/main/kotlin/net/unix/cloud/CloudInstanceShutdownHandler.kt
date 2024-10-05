package net.unix.cloud

import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudShutdownEvent

class CloudInstanceShutdownHandler(
    private val instance: CloudInstance
) {

    fun run() {
        CloudShutdownEvent().callEvent()
        instance.server.close()
        instance.terminal.close()
    }

}