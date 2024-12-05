package net.unix.node.group

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceExecutable
import net.unix.node.service.CloudServiceJVMExecutable

object GroupJVMExecutable : AbstractCloudGroupExecutable() {

    private fun readResolve(): Any = GroupJVMExecutable

    override val name: String = "JVM"

    override fun executableFor(service: CloudService): CloudServiceExecutable {
        return CloudServiceJVMExecutable(service)
    }

}