package net.unix.module.rest.defaultcontroller.service

import net.unix.api.service.Service
import net.unix.api.service.ServiceManager
import net.unix.api.service.wrapper.ConsoleServiceWrapper
import net.unix.module.rest.annotation.*
import net.unix.module.rest.controller.Controller
import net.unix.module.rest.defaultcontroller.dto.CommandDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*

@Suppress("unused")
@RestController("cloud/action/service/")
class ServiceActionController : Controller, KoinComponent {

    private val serviceManager: ServiceManager by inject(named("default"))

    @RequestMapping(RequestType.POST, "name/:uuid/kill/:delete", "web.cloud.action.service.kill")
    fun handleServiceKill(@RequestPathParam("uuid") uuid: String, @RequestPathParam("delete") delete: Boolean): Service {
        val service = serviceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()
        service.kill(delete)
        return service
    }

    @RequestMapping(RequestType.POST, "name/:uuid/execute", "web.cloud.action.service.execute")
    fun handleCommandExecute(@RequestPathParam("uuid") uuid: String, @RequestBody command: CommandDto): Service {
        val service = serviceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        (service.wrapper as? ConsoleServiceWrapper)?.command(command.command)

        return service
    }

    @RequestMapping(RequestType.POST,"name/:uuid/screen", "web.cloud.action.service.screen")
    fun handleScreenToggle(@RequestPathParam("uuid") uuid: String): Service {
        val service = serviceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        val executable = (service.wrapper as? ConsoleServiceWrapper)

        executable?.viewConsole = !(executable?.viewConsole ?: false)

        return service
    }

    @RequestMapping(RequestType.GET, "name/:uuid/uptime", "web.cloud.action.service.uptime")
    fun handleUptime(@RequestPathParam("uuid") uuid: String): Long {
        val service = serviceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        return service.uptime
    }

    @RequestMapping(RequestType.GET, "name/:uuid/uptime", "web.cloud.action.service.created")
    fun handleCreated(@RequestPathParam("uuid") uuid: String): Long {
        val service = serviceManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        return service.created
    }

}