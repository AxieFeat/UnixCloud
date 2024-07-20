package net.unix.api.terminal

import net.unix.api.command.sender.CommandSender
import org.jline.reader.LineReader
import org.jline.terminal.Terminal

/**
 * UnixCloud terminal
 */
interface JLineTerminal {

    /**
     * Terminal command sender
     */
    val sender: CommandSender

    /**
     * Default terminal prompt
     */
    val defaultPrompt: String

    /**
     * Instance of [Terminal] from JLine
     */
    val terminal: Terminal

    /**
     * Instance of [LineReader] from JLine
     */
    val lineReader: LineReader

    /**
     * Close terminal
     */
    fun close()

    /**
     * Print text in terminal
     *
     * @param input Text to print
     */
    fun print(input: String)
}