package net.unix.module.rest.defaultcontroller.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.api.service.CloudService
import net.unix.module.rest.annotation.*
import net.unix.module.rest.controller.Controller
import net.unix.node.CloudExtension.uniqueUUID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*

@Suppress("unused")
@RestController("cloud/group/")
class GroupController : Controller, KoinComponent {

    private val cloudGroupManager: CloudGroupManager by inject(named("default"))

    @RequestMapping(RequestType.GET, "", "web.cloud.group.get.all")
    fun handleGetAllGroups(): Set<CloudGroup> {
        return cloudGroupManager.groups
    }

    @RequestMapping(RequestType.GET, "uuid/:uuid", "web.cloud.group.get.one")
    fun handleGetOneGroup(@RequestPathParam("uuid") uuid: String): CloudGroup {
        return cloudGroupManager[UUID.fromString(uuid)] ?: throwNoSuchElement()
    }

    @RequestMapping(RequestType.GET, "uuid/:uuid/services", "web.cloud.group.get.services")
    fun handleGetServicesOfGroup(@RequestPathParam("uuid") uuid: String): Set<CloudService> {
        return cloudGroupManager[UUID.fromString(uuid)]?.services ?: throwNoSuchElement()
    }

    @RequestMapping(RequestType.POST, "create/:name", "web.cloud.group.create")
    fun handleCreateGroup(@RequestPathParam("name") name: String): CloudGroup {
        return cloudGroupManager.newInstance(
            uniqueUUID(),
            name,
            1,
            "main.jar"
        )
    }

    //delete groups
    @RequestMapping(RequestType.DELETE, "uuid/:uuid/delete", "web.cloud.group.delete")
    fun handleDeleteServiceGroup(@RequestPathParam("uuid") uuid: String): Boolean {
        val groupUUID = UUID.fromString(uuid)

        if (!doesGroupExist(groupUUID)) throwNoSuchElement()

        val group = cloudGroupManager[groupUUID]!!
        cloudGroupManager.unregister(group)
        return true
    }

    private fun doesGroupExist(uuid: UUID): Boolean {
        return cloudGroupManager[uuid] != null
    }

}