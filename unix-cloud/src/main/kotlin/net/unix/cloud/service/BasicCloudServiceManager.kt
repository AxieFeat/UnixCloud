package net.unix.cloud.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import java.util.*

object BasicCloudServiceManager : CloudServiceManager {

    override val services: Set<CloudService> = setOf()

    override fun get(name: String): List<CloudService> {
        TODO("Not yet implemented")
    }

    override fun get(uuid: UUID): CloudService? {
        TODO("Not yet implemented")
    }

}