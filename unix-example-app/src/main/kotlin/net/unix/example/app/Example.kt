package net.unix.example.app

import net.unix.api.LocationSpace
import net.unix.api.group.CloudGroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.driver.JVMServiceInstance
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun main() {
    JVMServiceInstance.install()

    ExampleService().start()
}

@Suppress("unused")
class ExampleService : KoinComponent {

    private val locationSpace: LocationSpace by inject()
    private val cloudTemplateManager: CloudTemplateManager by inject()
    private val cloudGroupManager: CloudGroupManager by inject()
    private val cloudServiceManager: CloudServiceManager by inject()
    private val extensionManager: ExtensionManager by inject()
    private val moduleManager: ModuleManager by inject()

    fun start() {

    }
}