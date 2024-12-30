package net.unix.node.command.format

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.terminal.Terminal
import net.unix.node.command.question.CloudQuestionManager
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.CloudTerminalCompleteEvent
import net.unix.command.sender.CommandSender
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

open class CloudCommandCompleter(
    private val sender: CommandSender
) : Completer, KoinComponent {

    private val terminal: Terminal by inject(named("default"))
    private val dispatcher: net.unix.command.CommandDispatcher by inject(named("default"))

    companion object {
        fun prepareStringReader(line: String): StringReader {
            val stringReader = StringReader(line)

            if (stringReader.canRead() && stringReader.peek() == '/') {
                stringReader.skip()
            }

            return stringReader
        }
    }

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {

        val event = CloudTerminalCompleteEvent(reader, line, candidates).callEvent()

        if (event.cancelled) {
            return
        }

        val activeQuestion = CloudQuestionManager.activeQuestion

        if(activeQuestion != null) {

            val result = activeQuestion.argument.suggestion(
                terminal.sender,
                SuggestionsBuilder(
                    line.line(),
                    line.line().lowercase(),
                    0
                )
            ).join().list.map { Candidate(it.text) }

            candidates.addAll(result)

            return
        }

        val dispatcher: CommandDispatcher<CommandSender> = dispatcher.dispatcher

        val stringLine = line.line()

        if (stringLine.startsWith("/")) return

        val results: ParseResults<CommandSender> = dispatcher.parse(
            prepareStringReader(stringLine),
            sender
        )

        val cursor = line.cursor()

        candidates.addAll(
            dispatcher.getCompletionSuggestions(results, cursor).join().list.map {
                Candidate(it.text)
            }
        )
    }
}