package net.unix.node.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceStatus
import net.unix.api.service.CloudServiceWrapper
import java.io.File

abstract class AbstractCloudExecutableCloud(
    override val service: CloudService,
    override val executableFile: File = File(service.dataFolder, service.group.executableFile)
) : CloudServiceWrapper {

    override var started: Boolean = false
        set(value) {
            field = value

            if (value) service.status = CloudServiceStatus.STARTED
            else service.status = CloudServiceStatus.PREPARED
        }

}