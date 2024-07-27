package net.unix.cloud.terminal

import net.kyori.adventure.text.Component
import net.unix.api.CloudExtension.deserializeComponent
import net.unix.api.CloudExtension.serializeAnsi
import net.unix.api.command.sender.CommandSender
import net.unix.api.terminal.JLineTerminal
import net.unix.cloud.command.brigadier.BrigadierCommandCompleter
import net.unix.cloud.command.brigadier.BrigadierCommandHighlighter
import net.unix.cloud.command.sender.ConsoleCommandSenderImpl
import net.unix.cloud.persistence.PersistentDataContainerImpl
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.nio.charset.StandardCharsets

class JLineTerminalImpl(
    prompt: String
) : JLineTerminal {

    override val defaultPrompt = prompt.deserializeComponent()

    override val sender: CommandSender = ConsoleCommandSenderImpl()

    override val terminal: Terminal = TerminalBuilder.builder()
        .system(true)
        .dumb(true)
        .encoding(StandardCharsets.UTF_8)
        .streams(System.`in`, System.out)
        .build()

    override val lineReader: LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(BrigadierCommandCompleter)
        .highlighter(BrigadierCommandHighlighter)
        .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
        .option(LineReader.Option.AUTO_PARAM_SLASH, false)
        .option(LineReader.Option.AUTO_GROUP, false)
        .option(LineReader.Option.INSERT_TAB, false)
        .build()

    init {
        lineReader.autosuggestion = LineReader.SuggestionType.COMPLETER
    }

    var currentPrompt = defaultPrompt

    private val runner: JLineTerminalRunner = JLineTerminalRunner(this)

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

    override val persistentDataContainer = PersistentDataContainerImpl()

    private fun redraw() {
        if (lineReader.isReading) {
            lineReader.callWidget("redraw-line")
            lineReader.callWidget("redisplay")
        }
    }
}