package net.unix.cloud.terminal

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.command.CommandDispatcher
import net.unix.cloud.CloudExtension.serializeAnsi
import net.unix.cloud.CloudExtension.strip
import net.unix.cloud.CloudInstance
import net.unix.cloud.command.aether.SyntaxExceptionBuilder
import net.unix.cloud.configuration.UnixConfiguration
import net.unix.scheduler.impl.scheduler
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

                trim = line.trim()

                if (trim.isNotEmpty()) {

                    if (!line.startsWith(UnixConfiguration.terminal.serviceCommandPrefix)) {
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
                    } else {
                        CloudInstance.instance.terminal.selectedExecutable?.command(
                            line.replaceFirst(UnixConfiguration.terminal.serviceCommandPrefix, "")
                        )
                    }

                }

            }

        }
    }

}