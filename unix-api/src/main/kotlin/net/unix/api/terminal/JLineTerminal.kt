package net.unix.api.terminal

import net.unix.api.command.sender.CommandSender
import org.jline.reader.LineReader
import org.jline.terminal.Terminal

interface JLineTerminal {

    /**
     * Отправитель команды
     */
    val sender: CommandSender

    /**
     * Строка для терминала
     */
    val terminalLine: String

    val terminal: Terminal
    val lineReader: LineReader

    /**
     * Закрытие терминала
     */
    fun close()

    /**
     * Вывод текста в терминал
     */
    fun print(input: String)
}