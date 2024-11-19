package net.unix.cloud

import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudShutdownEvent
import net.unix.cloud.logging.CloudLogger

class CloudInstanceShutdownHandler(
    private val instance: CloudInstance
) {

    fun run() {
        CloudLogger.info("Stopping UnixCloud...")
        CloudShutdownEvent().callEvent()
        instance.server.close()
        instance.terminal.close()
    }

}