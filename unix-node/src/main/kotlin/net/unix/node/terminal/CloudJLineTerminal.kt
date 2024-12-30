package net.unix.node.terminal

import net.kyori.adventure.text.Component
import net.unix.api.service.wrapper.ConsoleServiceWrapper
import net.unix.node.command.format.CloudCommandCompleter
import net.unix.node.command.format.CloudCommandHighlighter
import net.unix.node.command.sender.CloudConsoleCommandSender
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.persistence.CloudPersistentDataContainer
import net.unix.command.sender.CommandSender
import net.unix.node.CloudExtension.deserializeComponent
import net.unix.node.CloudExtension.serializeAnsi
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.nio.charset.StandardCharsets

@Suppress("LeakingThis", "MemberVisibilityCanBePrivate")
open class CloudJLineTerminal(
    override val defaultPrompt: Component = UnixConfiguration.terminal.prompt.deserializeComponent()
) : net.unix.api.terminal.Terminal {

    override val persistentDataContainer = CloudPersistentDataContainer()

    var currentPrompt = defaultPrompt

    override val sender: CommandSender = CloudConsoleCommandSender()

    override var selectedExecutable: ConsoleServiceWrapper? = null

    private lateinit var runner: JLineTerminalRunner

    val terminal: org.jline.terminal.Terminal = TerminalBuilder.builder()
        .system(true)
        .dumb(true)
        .encoding(StandardCharsets.UTF_8)
        .streams(System.`in`, System.out)
        .build()

    val lineReader: LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(CloudCommandCompleter(sender))
        .highlighter(CloudCommandHighlighter(sender))
        .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
        .option(LineReader.Option.AUTO_PARAM_SLASH, false)
        .option(LineReader.Option.AUTO_GROUP, false)
        .option(LineReader.Option.INSERT_TAB, false)
        .build().also {
            it.autosuggestion = LineReader.SuggestionType.COMPLETER
        }

    override fun start() {
        runner = JLineTerminalRunner(this)
        runner.start()
    }

    override fun close() {
        runner.interrupt()
        terminal.close()
    }

    override fun clear() {

        repeat((1..100).count()) {
            terminal.writer().println(" ")
        }

    }

    override fun setPrompt(component: Component?) {
        currentPrompt = component ?: defaultPrompt
        runner.updatePrompt()
    }

    override fun print(component: Component) {
        terminal.puts(InfoCmp.Capability.carriage_return)
        terminal.writer().println(component.serializeAnsi())
        redraw()
    }

    override fun print(message: String) = print(message.deserializeComponent())

    private fun redraw() {
        if (lineReader.isReading) {
            lineReader.callWidget("redraw-line")
            lineReader.callWidget("redisplay")
        }
    }
}