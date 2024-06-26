package net.unix.cloud.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager

object CloudServiceManagerImpl : CloudServiceManager {

    override fun getService(name: String): CloudService? {
        return null
    }

}