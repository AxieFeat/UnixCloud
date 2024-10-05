package net.unix.api.terminal

import net.kyori.adventure.text.Component
import net.unix.api.command.sender.CommandSender
import net.unix.api.persistence.PersistentDataHolder

/**
 * Terminal, just terminal.
 */
interface Terminal : PersistentDataHolder {

    /**
     * Terminal command sender.
     */
    val sender: CommandSender

    /**
     * Default terminal prompt.
     */
    val defaultPrompt: Component

    /**
     * Set prompt for terminal.
     *
     * @param component Prompt. Null for reset to [defaultPrompt].
     */
    fun setPrompt(component: Component?)

    /**
     * Close terminal.
     */
    fun close()

    /**
     * Print [Component] in terminal.
     *
     * @param component [Component] to print.
     */
    fun print(component: Component)

    companion object

}