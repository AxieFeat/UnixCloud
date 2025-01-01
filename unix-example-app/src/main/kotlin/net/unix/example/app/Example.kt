package net.unix.example.app

import net.unix.api.LocationSpace
import net.unix.api.group.GroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.service.ServiceManager
import net.unix.api.template.TemplateManager
import net.unix.driver.DriverServiceInstance
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*

fun main() {
    val instance = DriverServiceInstance()

    instance.install()

    ExampleService().start(instance)
}

@Suppress("unused")
class ExampleService : KoinComponent {

    private val locationSpace: LocationSpace by inject(named("default"))
    private val templateManager: TemplateManager by inject(named("default"))
    private val groupManager: GroupManager by inject(named("default"))
    private val serviceManager: ServiceManager by inject(named("default"))
    private val extensionManager: ExtensionManager by inject(named("default"))
    private val moduleManager: ModuleManager by inject(named("default"))

    fun start(instance: DriverServiceInstance) {

        println("I am ${instance.service}")

        val scanner = Scanner(System.`in`)

        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()

            println("Your write: $line")
        }
    }
}