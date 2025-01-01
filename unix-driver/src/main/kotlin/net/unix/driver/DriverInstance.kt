package net.unix.driver

import net.unix.api.LocationSpace
import net.unix.api.group.GroupManager
import net.unix.api.group.wrapper.GroupWrapperFactoryManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.service.ServiceManager
import net.unix.api.template.TemplateManager
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry

@Suppress("MemberVisibilityCanBePrivate")
open class DriverInstance(
    val host: String = "localhost",
    val rmiPort: Int = 1099
) {

    lateinit var registry: Registry
    lateinit var locationSpace: LocationSpace
    lateinit var templateManager:TemplateManager
    lateinit var groupWrapperFactoryManager: GroupWrapperFactoryManager
    lateinit var groupManager: GroupManager
    lateinit var serviceManager: ServiceManager
    lateinit var moduleManager: ModuleManager
    lateinit var extensionManager: ExtensionManager

    open fun install() {
        println("Driver instance installing...")

        registry = LocateRegistry.getRegistry(host, rmiPort)
        locationSpace = registry.find<LocationSpace>()
        templateManager = registry.find<TemplateManager>()
        groupWrapperFactoryManager = registry.find<GroupWrapperFactoryManager>()
        groupManager = registry.find<GroupManager>()
        serviceManager = registry.find<ServiceManager>()
        moduleManager = registry.find<ModuleManager>()
        extensionManager = registry.find<ExtensionManager>()

        println("Driver instance installed.")
    }

    private inline fun <reified T> Registry.find(): T {
        return this.lookup(T::class.simpleName) as T
    }

}