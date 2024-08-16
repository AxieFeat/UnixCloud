package net.unix.cloud.terminal

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.CloudExtension.serializeAnsi
import net.unix.api.CloudExtension.strip
import net.unix.api.command.CommandDispatcher
import net.unix.api.command.aether.SyntaxExceptionBuilder
import net.unix.api.scheduler.scheduler
import net.unix.api.terminal.Terminal
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.jline.reader.EndOfFileException
import org.jline.reader.UserInterruptException

class JLineTerminalRunner(
    private val terminal: CloudJLineTerminal,
    private val dispatcher: CommandDispatcher
) : Thread() {

    private var strippedPrompt = terminal.currentPrompt.strip()
    private var ansiPrompt = terminal.currentPrompt.serializeAnsi()

    fun updatePrompt() {
        strippedPrompt = terminal.currentPrompt.strip()
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
            var line: String
            var trim: String

            while (!currentThread().isInterrupted) {

                try {
                    line = terminal.lineReader.readLine(ansiPrompt)
                } catch (e: EndOfFileException) {
                   continue
                } catch (e: UserInterruptException) {
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