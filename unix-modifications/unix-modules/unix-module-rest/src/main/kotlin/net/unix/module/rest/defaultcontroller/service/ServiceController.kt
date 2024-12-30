package net.unix.module.rest.defaultcontroller.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.api.service.wrapper.ConsoleCloudServiceWrapper
import net.unix.module.rest.annotation.*
import net.unix.module.rest.controller.Controller
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*

@Suppress("unused")
@RestController("cloud/service/")
class ServiceController : Controller, KoinComponent {

    private val cloudServiceManager: CloudServiceManager by inject(named("default"))

    @RequestMapping(RequestType.GET, "", "web.cloud.service.get.all")
    fun handleGetAllServices(): Set<CloudService> {
        return cloudServiceManager.services
    }

    @RequestMapping(RequestType.GET, "name/:name", "web.cloud.service.get-name.one")
    fun handleGetOneServiceByName(@RequestPathParam("name") name: String): List<CloudService> {
        return cloudServiceManager[name]
    }

    @RequestMapping(RequestType.GET, "name/:name", "web.cloud.service.get-uuid.one")
    fun handleGetOneServiceByUuid(@RequestPathParam("uuid") uuid: String): CloudService {
        return cloudServiceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()
    }

    @RequestMapping(RequestType.GET, "logs/:name", "web.cloud.service.logs")
    fun handleGetServiceLogs(@RequestPathParam("uuid") uuid: String): List<String> {
        val serviceUuid = UUID.fromString(uuid)

        if (!doesServiceExist(serviceUuid)) throwNoSuchElement()

        val service = cloudServiceManager[serviceUuid]!!

        return (service.wrapper as? ConsoleCloudServiceWrapper)?.logs ?: throwNoSuchElement()
    }

    private fun doesServiceExist(uuid: UUID): Boolean {
        return cloudServiceManager.services.any { it.uuid == uuid }
    }


}