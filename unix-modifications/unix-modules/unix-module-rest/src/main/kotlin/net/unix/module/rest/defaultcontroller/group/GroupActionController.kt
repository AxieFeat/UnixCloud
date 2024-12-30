package net.unix.module.rest.defaultcontroller.group

import net.unix.api.group.GroupManager
import net.unix.api.service.Service
import net.unix.module.rest.annotation.RequestMapping
import net.unix.module.rest.annotation.RequestPathParam
import net.unix.module.rest.annotation.RequestType
import net.unix.module.rest.annotation.RestController
import net.unix.module.rest.controller.Controller
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*

@Suppress("unused")
@RestController("cloud/action/group/")
class GroupActionController : Controller, KoinComponent {

    private val groupManager: GroupManager by inject(named("default"))

    @RequestMapping(RequestType.POST, "uuid/:uuid/startService", "web.cloud.action.group.startservice")
    fun handleStartNewService(@RequestPathParam("uuid") uuid: String): Service {
        val group = groupManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        return group.create()
    }

}