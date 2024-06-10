package net.unix.api

import net.unix.api.service.ServiceManager

abstract class CloudAPI {

    companion object {
        lateinit var instance: CloudAPI
    }

    init {
        instance = this
    }

    abstract val serviceManager: ServiceManager
}