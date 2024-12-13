package net.unix.node.group

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceWrapper
import net.unix.node.service.CloudServiceJVMWrapper

object GroupJVMWrapper : AbstractCloudGroupWrapper() {

    private fun readResolve(): Any = GroupJVMWrapper

    override val name: String = "JVM"

    override fun executableFor(service: CloudService): CloudServiceWrapper {
        return CloudServiceJVMWrapper(service)
    }

}