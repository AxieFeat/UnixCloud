package net.unix.cloud.terminal

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.CloudExtension.print
import net.unix.api.CloudExtension.serializeAnsi
import net.unix.api.CloudExtension.strip
import net.unix.api.command.aether.SyntaxExceptionBuilder
import net.unix.api.scheduler.scheduler
import net.unix.api.terminal.JLineTerminal
import net.unix.cloud.CloudInstance
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.jline.reader.EndOfFileException

class JLineTerminalRunner(
    private val terminal: JLineTerminal
) : Thread() {

    private var strippedPrompt = (terminal as JLineTerminalImpl).currentPrompt.strip()
    private var ansiPrompt = (terminal as JLineTerminalImpl).currentPrompt.serializeAnsi()

    fun updatePrompt() {
        strippedPrompt = (terminal as JLineTerminalImpl).currentPrompt.strip()
        ansiPrompt = terminal.currentPrompt.serializeAnsi()
    }

    init {
        isDaemon = false
        name = "TerminalRunner"
        priority = 1
        start()
    }

    override fun run() {
        scheduler {
            val dispatcher = CloudInstance.commandDispatcher

            var line: String
            var trim: String

            while (!currentThread().isInterrupted) {

                try {
                    line = terminal.lineReader.readLine(ansiPrompt)
                } catch (e: EndOfFileException) {
                   continue
                }

                execute {
                    LogManager.getLogger("info").log(Level.getLevel("INFO"), "${strippedPrompt}${line}")
                }

                trim = line.trim()

                if (trim.isNotEmpty() && !line.startsWith("/")) {

                    execute {
                        try {

                            dispatcher.dispatchCommand(
                                dispatcher.parseCommand(
                                    terminal.sender,
                                    trim
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

}