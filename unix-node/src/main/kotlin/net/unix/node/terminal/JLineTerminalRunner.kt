package net.unix.node.terminal

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.node.command.question.CloudQuestionManager
import net.unix.node.configuration.UnixConfiguration
import net.unix.command.CommandDispatcher
import net.unix.command.question.exception.QuestionParseException
import net.unix.node.CloudExtension.serializeAnsi
import net.unix.node.CloudExtension.strip
import net.unix.node.logging.CloudLogger
import net.unix.scheduler.impl.scheduler
import org.jline.reader.EndOfFileException
import org.jline.reader.UserInterruptException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

val unixStartTime = System.currentTimeMillis()

val unixUptime: Long
    get() = System.currentTimeMillis() - unixStartTime

class JLineTerminalRunner(
    val terminal: CloudJLineTerminal
) : Thread(), KoinComponent {

    private val dispatcher: CommandDispatcher by inject(named("default"))

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

                val activeQuestion = CloudQuestionManager.activeQuestion

                if (activeQuestion != null) {
                    try {
                        activeQuestion.answer = activeQuestion.argument.parse(StringReader(line))
                    } catch (e: QuestionParseException) {
                        e.message?.let { CloudLogger.severe(it) }
                    }
                    continue
                }

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
                        terminal.selectedExecutable?.command(
                            line.replaceFirst(UnixConfiguration.terminal.serviceCommandPrefix, "")
                        )
                    }

                }

            }

        }
    }

}