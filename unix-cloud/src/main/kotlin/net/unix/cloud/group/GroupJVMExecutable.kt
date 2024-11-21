package net.unix.cloud.group

import net.unix.api.group.GroupExecutable
import net.unix.api.service.CloudService
import net.unix.api.service.ServiceExecutable
import net.unix.cloud.service.ServiceJVMExecutable

object GroupJVMExecutable : GroupExecutable() {

    override val name: String = "JVM"

    override fun executableFor(service: CloudService): ServiceExecutable {
        return ServiceJVMExecutable(service)
    }

}