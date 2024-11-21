package net.unix.cloud

import net.unix.api.service.StaticCloudService
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudShutdownEvent
import net.unix.cloud.logging.CloudLogger

class CloudInstanceShutdownHandler(
    private val instance: CloudInstance
) {

    fun run() {
        CloudShutdownEvent().callEvent()

        CloudLogger.info("Stopping UnixCloud...")

        CloudInstance.instance.cloudServiceManager.services.forEach {
            if (it !is StaticCloudService || !it.static) {
                try {
                    it.kill(true)
                } catch (ignore: Exception) {}
            }
        }
        instance.server.close()
        instance.terminal.close()
    }

}