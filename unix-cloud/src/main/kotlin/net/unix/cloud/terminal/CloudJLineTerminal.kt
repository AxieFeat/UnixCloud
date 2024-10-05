package net.unix.cloud.terminal

import net.kyori.adventure.text.Component
import net.unix.api.command.CommandDispatcher
import net.unix.api.command.sender.CommandSender
import net.unix.api.terminal.logger.Logger
import net.unix.cloud.CloudExtension.deserializeComponent
import net.unix.cloud.CloudExtension.serializeAnsi
import net.unix.cloud.command.brigadier.BrigadierCommandCompleter
import net.unix.cloud.command.brigadier.BrigadierCommandHighlighter
import net.unix.cloud.command.sender.CloudConsoleCommandSender
import net.unix.cloud.persistence.CloudPersistentDataContainer
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.nio.charset.StandardCharsets

@Suppress("LeakingThis", "MemberVisibilityCanBePrivate")
open class CloudJLineTerminal(
    prompt: String,
    logger: Logger,
    dispatcher: CommandDispatcher
) : net.unix.api.terminal.Terminal {

    override val defaultPrompt = prompt.deserializeComponent()

    override val sender: CommandSender = CloudConsoleCommandSender(logger)

    val terminal: org.jline.terminal.Terminal = TerminalBuilder.builder()
        .system(true)
        .dumb(true)
        .encoding(StandardCharsets.UTF_8)
        .streams(System.`in`, System.out)
        .build()

    val lineReader: LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(BrigadierCommandCompleter(sender))
        .highlighter(BrigadierCommandHighlighter(sender))
        .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
        .option(LineReader.Option.AUTO_PARAM_SLASH, false)
        .option(LineReader.Option.AUTO_GROUP, false)
        .option(LineReader.Option.INSERT_TAB, false)
        .build()

    init {
        lineReader.autosuggestion = LineReader.SuggestionType.COMPLETER
    }

    var currentPrompt = defaultPrompt

    private val runner: JLineTerminalRunner = JLineTerminalRunner(this, dispatcher)

    override fun setPrompt(component: Component?) {
        currentPrompt = component ?: defaultPrompt
        runner.updatePrompt()
    }

    override fun close() {
        runner.interrupt()
        terminal.close()
    }

    override fun print(component: Component) {
        terminal.puts(InfoCmp.Capability.carriage_return)
        terminal.writer().println(component.serializeAnsi())
        redraw()
    }

    override val persistentDataContainer = CloudPersistentDataContainer()

    private fun redraw() {
        if (lineReader.isReading) {
            lineReader.callWidget("redraw-line")
            lineReader.callWidget("redisplay")
        }
    }
}