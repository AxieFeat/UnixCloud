package net.unix.cloud.terminal

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.cloud.CloudExtension.serializeAnsi
import net.unix.cloud.CloudExtension.strip
import net.unix.cloud.command.aether.SyntaxExceptionBuilder
import net.unix.cloud.command.question.CloudQuestionManager
import net.unix.cloud.configuration.UnixConfiguration
import net.unix.cloud.logging.CloudLogger
import net.unix.command.CommandDispatcher
import net.unix.command.question.exception.QuestionParseException
import net.unix.scheduler.impl.scheduler
import org.jline.reader.EndOfFileException
import org.jline.reader.UserInterruptException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class JLineTerminalRunner(
    val terminal: CloudJLineTerminal
) : Thread(), KoinComponent {

    private val dispatcher: CommandDispatcher by inject()

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

                    val activeQuestion = CloudQuestionManager.activeQuestion

                    if (activeQuestion != null) {
                        try {
                            activeQuestion.answer = activeQuestion.argument.parse(StringReader(line))
                        } catch (e: QuestionParseException) {
                            e.message?.let { CloudLogger.severe(it) }
                        }
                        continue
                    }

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
                        terminal.selectedExecutable?.command(
                            line.replaceFirst(UnixConfiguration.terminal.serviceCommandPrefix, "")
                        )
                    }

                }

            }

        }
    }

}