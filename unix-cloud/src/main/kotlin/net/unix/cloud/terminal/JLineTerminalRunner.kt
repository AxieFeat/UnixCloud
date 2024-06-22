package net.unix.cloud.terminal

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.parseColor
import net.unix.api.terminal.JLineTerminal
import net.unix.cloud.CloudInstance

class JLineTerminalRunner(
    private val terminal: JLineTerminal
) : Thread() {

    init {
        isDaemon = false
        name = "TerminalRunner"
        priority = 1
        start()
    }


    override fun run() {

        var line: String

        while (!currentThread().isInterrupted) {

            line = terminal.lineReader.readLine(terminal.terminalLine.parseColor())

            if (line.trim().isNotEmpty() && !line.startsWith("/")) {

                //line = line.replaceFirst("/", "").trim()

                try {

                    val dispatcher = CloudInstance.commandDispatcher

                    dispatcher.dispatchCommand(
                        dispatcher.parseCommand(
                            terminal.sender,
                            line.trim()
                        )
                    )

                } catch (e: CommandSyntaxException) {
                    terminal.print(
                        e.message!!
                    )
                }

            }
        }

    }

}