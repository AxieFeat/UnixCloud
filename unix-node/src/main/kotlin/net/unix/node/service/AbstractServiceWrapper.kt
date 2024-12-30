package net.unix.node.service

import net.unix.api.service.Service
import net.unix.api.service.ServiceStatus
import net.unix.api.service.wrapper.ServiceWrapper
import java.io.File

abstract class AbstractServiceWrapper(
    override val service: Service,
    override val executableFile: File = File(service.dataFolder, service.group.wrapper?.executableFile ?: "service.jar")
) : ServiceWrapper {

    override var started: Boolean = false
        set(value) {
            field = value

            if (value) service.status = ServiceStatus.STARTED
            else service.status = ServiceStatus.PREPARED
        }

}