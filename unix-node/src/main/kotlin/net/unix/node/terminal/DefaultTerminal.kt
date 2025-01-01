package net.unix.node.terminal

import net.kyori.adventure.text.Component
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.wrapper.ConsoleServiceWrapper
import net.unix.api.terminal.Terminal
import net.unix.command.sender.CommandSender
import net.unix.node.CloudExtension.deserializeComponent
import net.unix.node.CloudExtension.serializeAnsi
import net.unix.node.command.sender.CloudConsoleCommandSender
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.persistence.CloudPersistentDataContainer
import java.io.PrintWriter
import java.nio.charset.Charset

/**
 * This class represents a terminal without any libs.
 */
class DefaultTerminal : Terminal {

    override val sender: CommandSender = CloudConsoleCommandSender()

    override val defaultPrompt: Component = UnixConfiguration.terminal.prompt.deserializeComponent()

    override var selectedExecutable: ConsoleServiceWrapper? = null

    private lateinit var runner: DefaultTerminalRunner

    private val writer = PrintWriter(System.out, true, Charset.forName("UTF-8"))

    override fun setPrompt(component: Component?) {
        // Not used in default implementation
    }

    override fun start() {
        runner = DefaultTerminalRunner(this)
        runner.start()
    }

    override fun close() {
        runner.interrupt()
    }

    override fun clear() {
        repeat((1..100).count()) {
            printMessage(" ")
        }
    }

    override fun print(component: Component) {
        printMessage(component.serializeAnsi())
    }

    override fun print(message: String) {
        printMessage(message.deserializeComponent().serializeAnsi())
    }

    private fun printMessage(message: String) {
        writer.println(message)
    }

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    companion object
}