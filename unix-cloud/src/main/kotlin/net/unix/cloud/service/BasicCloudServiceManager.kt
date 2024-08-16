package net.unix.cloud.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager

object BasicCloudServiceManager : CloudServiceManager {

    override val services: Set<CloudService> = setOf()

    override operator fun get(name: String): CloudService? {
        return null
    }

}