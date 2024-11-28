package net.unix.command.question

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import java.util.concurrent.CompletableFuture
import kotlin.jvm.Throws

/**
 * This interface represents an argument of question.
 *
 * @param T Type of argument.
 */
interface QuestionArgument<T> {

    /**
     * Parse argument.
     *
     * @param reader Reader.
     *
     * @return Instance of [T]
     *
     * @throws QuestionParseException If has parse error.
     */
    @Throws(QuestionParseException::class)
    fun parse(reader: StringReader): T

    /**
     * Suggestions for argument.
     *
     * @param sender Sender of command.
     * @param builder Builder for suggestions.
     *
     * @return Result of suggesting.
     */
    fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions>
}