package net.unix.module.rest.defaultcontroller.group

import net.unix.api.group.CloudGroupManager
import net.unix.api.service.CloudService
import net.unix.module.rest.annotation.RequestMapping
import net.unix.module.rest.annotation.RequestPathParam
import net.unix.module.rest.annotation.RequestType
import net.unix.module.rest.annotation.RestController
import net.unix.module.rest.controller.Controller
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@Suppress("unused")
@RestController("cloud/action/group/")
class GroupActionController : Controller, KoinComponent {

    private val cloudGroupManager: CloudGroupManager by inject()

    @RequestMapping(RequestType.POST, "uuid/:uuid/startService", "web.cloud.action.group.startservice")
    fun handleStartNewService(@RequestPathParam("uuid") uuid: String): CloudService {
        val group = cloudGroupManager[UUID.fromString(uuid)] ?: throwNoSuchElement()

        return group.create()
    }

}