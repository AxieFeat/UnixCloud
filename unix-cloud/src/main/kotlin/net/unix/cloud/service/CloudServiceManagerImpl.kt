package net.unix.cloud.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager

object CloudServiceManagerImpl : CloudServiceManager {
    override val services: List<CloudService>
        get() = TODO("Not yet implemented")

    override fun getService(name: String): CloudService? {
        return null
    }

}