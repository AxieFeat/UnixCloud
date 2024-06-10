package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.service.ServiceManager
import net.unix.cloud.service.ServiceManagerImpl

fun main() {
    CloudInstance()
}

class CloudInstance : CloudAPI() {

    companion object {
        lateinit var instance: CloudInstance
    }

    init {
        instance = this
    }

    override val serviceManager: ServiceManager = ServiceManagerImpl()

}