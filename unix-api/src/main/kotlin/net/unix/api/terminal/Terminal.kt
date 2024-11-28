package net.unix.api.terminal

import net.kyori.adventure.text.Component
import net.unix.api.pattern.Closeable
import net.unix.api.pattern.Startable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.service.ConsoleServiceExecutable
import net.unix.command.sender.CommandSender

/**
 * Terminal, just terminal.
 */
interface Terminal : PersistentDataHolder, Closeable, Startable {

    /**
     * Terminal command sender.
     */
    val sender: CommandSender

    /**
     * Default terminal prompt.
     */
    val defaultPrompt: Component

    /**
     * Selected executable. It needs for command sending to services.
     */
    var selectedExecutable: ConsoleServiceExecutable?

    /**
     * Set prompt for terminal.
     *
     * @param component Prompt. Null for reset to [defaultPrompt].
     */
    fun setPrompt(component: Component?)

    /**
     * Start terminal
     */
    override fun start()

    /**
     * Close terminal.
     */
    override fun close()

    /**
     * Clear the terminal.
     */
    fun clear()

    /**
     * Print [Component] in terminal.
     *
     * @param component [Component] to print.
     */
    fun print(component: Component)

    /**
     * Print string in terminal. String will be deserialized with MiniMessage to Component.
     *
     * @param message Message to print.
     */
    fun print(message: String)

    companion object

}