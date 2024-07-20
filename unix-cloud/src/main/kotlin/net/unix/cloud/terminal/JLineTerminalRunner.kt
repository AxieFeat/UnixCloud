package net.unix.cloud.terminal

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.CloudExtension.stripColor
import net.unix.api.command.aether.SyntaxExceptionBuilder
import net.unix.api.scheduler.scheduler
import net.unix.api.terminal.JLineTerminal
import net.unix.cloud.CloudInstance
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager

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
        scheduler {

            var line: String

            while (!currentThread().isInterrupted) {

                line = terminal.lineReader.readLine(terminal.defaultPrompt)

                execute {
                    LogManager.getLogger("info").log(Level.getLevel("INFO"),  "${terminal.defaultPrompt.stripColor()}$line")
                }

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
                        SyntaxExceptionBuilder.print(e)
                    }

                }
            }

        }
    }

}