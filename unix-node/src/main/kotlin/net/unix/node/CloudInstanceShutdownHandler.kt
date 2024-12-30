package net.unix.node

import net.unix.api.ShutdownHandler
import net.unix.api.network.server.Server
import net.unix.api.service.CloudServiceManager
import net.unix.api.service.StaticCloudService
import net.unix.api.terminal.Terminal
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.CloudShutdownEvent
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class CloudInstanceShutdownHandler : KoinComponent, ShutdownHandler {

    private val cloudServiceManager: CloudServiceManager by inject(named("default"))
    private val server: Server by inject(named("default"))
    private val terminal: Terminal by inject(named("default"))

    override fun run() {
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