package net.unix.cloud.terminal

import net.unix.api.CloudExtension.parseColor
import net.unix.api.command.sender.CommandSender
import net.unix.api.terminal.JLineTerminal
import net.unix.cloud.command.brigadier.BrigadierCommandCompleter
import net.unix.cloud.command.brigadier.BrigadierCommandHighlighter
import net.unix.cloud.command.sender.ConsoleCommandSenderImpl
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.nio.charset.StandardCharsets

class JLineTerminalImpl(
    prompt: String
) : JLineTerminal {

    override val defaultPrompt = prompt.parseColor()

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

    private val runner: JLineTerminalRunner

    init {
        lineReader.autosuggestion = LineReader.SuggestionType.COMPLETER

        runner = JLineTerminalRunner(this)
    }

    override fun close() {
        runner.interrupt()
        terminal.close()
    }

    override fun print(input: String) {
        terminal.puts(InfoCmp.Capability.carriage_return)
        terminal.writer().println(input)
        redraw()
    }

    private fun redraw() {
        if (lineReader.isReading) {
            lineReader.callWidget("redraw-line")
            lineReader.callWidget("redisplay")
        }
    }
}