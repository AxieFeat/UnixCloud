package net.unix.api.service

import java.io.File

abstract class AbstractCloudExecutable(
    override val service: CloudService,
    override val executableFile: File = File(service.dataFolder, service.group.executableFile)
) : ServiceExecutable {

    override var started: Boolean = false
        set(value) {
            field = value

            if (value) service.status = CloudServiceStatus.STARTED
            else service.status = CloudServiceStatus.PREPARED
        }

}