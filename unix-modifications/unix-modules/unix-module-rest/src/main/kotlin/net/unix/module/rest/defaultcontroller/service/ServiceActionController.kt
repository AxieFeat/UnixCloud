package net.unix.module.rest.defaultcontroller.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.api.service.ConsoleCloudServiceWrapper
import net.unix.module.rest.annotation.*
import net.unix.module.rest.controller.Controller
import net.unix.module.rest.defaultcontroller.dto.CommandDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@Suppress("unused")
@RestController("cloud/action/service/")
class ServiceActionController : Controller, KoinComponent {

    private val cloudServiceManager: CloudServiceManager by inject()

    @RequestMapping(RequestType.POST, "name/:uuid/kill/:delete", "web.cloud.action.service.kill")
    fun handleServiceKill(@RequestPathParam("uuid") uuid: String, @RequestPathParam("delete") delete: Boolean): CloudService {
        val service = cloudServiceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()
        service.kill(delete)
        return service
    }

    @RequestMapping(RequestType.POST, "name/:uuid/execute", "web.cloud.action.service.execute")
    fun handleCommandExecute(@RequestPathParam("uuid") uuid: String, @RequestBody command: CommandDto): CloudService {
        val service = cloudServiceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        (service.wrapper as? ConsoleCloudServiceWrapper)?.command(command.command)

        return service
    }

    @RequestMapping(RequestType.POST,"name/:uuid/screen", "web.cloud.action.service.screen")
    fun handleScreenToggle(@RequestPathParam("uuid") uuid: String): CloudService {
        val service = cloudServiceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        val executable = (service.wrapper as? ConsoleCloudServiceWrapper)

        executable?.viewConsole = !(executable?.viewConsole ?: false)

        return service
    }

    @RequestMapping(RequestType.GET, "name/:uuid/uptime", "web.cloud.action.service.uptime")
    fun handleUptime(@RequestPathParam("uuid") uuid: String): Long {
        val service = cloudServiceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        return service.uptime
    }

    @RequestMapping(RequestType.GET, "name/:uuid/uptime", "web.cloud.action.service.created")
    fun handleCreated(@RequestPathParam("uuid") uuid: String): Long {
        val service = cloudServiceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        return service.created
    }

}