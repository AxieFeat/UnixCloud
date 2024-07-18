package net.unix.cloud.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager

object CloudServiceManagerImpl : CloudServiceManager {
    override val services: List<CloudService> = listOf()

    override operator fun get(name: String): CloudService? {
        return null
    }

}