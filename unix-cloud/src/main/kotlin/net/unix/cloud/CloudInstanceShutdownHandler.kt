package net.unix.cloud

import net.unix.api.network.server.Server
import net.unix.api.service.CloudServiceManager
import net.unix.api.service.StaticCloudService
import net.unix.api.terminal.Terminal
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudShutdownEvent
import net.unix.cloud.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CloudInstanceShutdownHandler : KoinComponent {

    private val cloudServiceManager: CloudServiceManager by inject()
    private val server: Server by inject()
    private val terminal: Terminal by inject()

    fun run() {
        CloudShutdownEvent().callEvent()

        CloudLogger.info("Stopping UnixCloud...")

        cloudServiceManager.services.forEach {
            if (it !is StaticCloudService || !it.static) {
                try {
                    it.kill(true)
                } catch (ignore: Exception) {}
            }
        }
        server.close()
        terminal.close()
    }

}