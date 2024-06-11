package net.unix.cloud.service

import net.unix.api.service.CloudService
import net.unix.api.service.ServiceManager

object ServiceManagerImpl : ServiceManager {

    override fun getService(name: String): CloudService? {
        return null
    }

}