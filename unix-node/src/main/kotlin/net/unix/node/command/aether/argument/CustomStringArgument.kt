package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.command.CommandArgument
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture

@Suppress("MemberVisibilityCanBePrivate", "unused")
class CustomStringArgument private constructor(
    val type: StringType,
    val error: String,
    val suggestion: Callable<List<String>>
) : CommandArgument<String>() {

    override fun parse(reader: StringReader): String {
        if (this.type == StringType.GREEDY_PHRASE) {
            val text = reader.remaining
            reader.cursor = reader.totalLength

            if(!suggestion.call().contains(text)) throw SyntaxExceptionBuilder.exception(error, reader)

            return text
        } else {
            val text = if (this.type == StringType.SINGLE_WORD) reader.readUnquotedString() else reader.readString()

            if(!suggestion.call().contains(text)) throw SyntaxExceptionBuilder.exception(error, reader)

            return text
        }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {

        suggestion.call().forEach {
            if(it.contains(" ") && type == StringType.QUOTABLE_PHRASE) {
                builder.suggest("\"$it\"")
            } else {
                builder.suggest(it)
            }
        }

        return builder.buildFuture()
    }

    override fun toString(): String {
        return "customString()"
    }

    override fun getExamples(): Collection<String> {
        return type.examples
    }

    enum class StringType(vararg examples: String) {
        SINGLE_WORD(*arrayOf("word", "words_with_underscores")),
        QUOTABLE_PHRASE(*arrayOf("\"quoted phrase\"", "word", "\"\"")),
        GREEDY_PHRASE(*arrayOf("word", "words with spaces", "\"and symbols\""));

        val examples: Collection<String> = listOf(*examples)
    }

    companion object {
        fun word(error: String, callable: Callable<List<String>>): CustomStringArgument {
            return CustomStringArgument(StringType.SINGLE_WORD, error, callable)
        }

        fun string(error: String, callable: Callable<List<String>>): CustomStringArgument {
            return CustomStringArgument(StringType.QUOTABLE_PHRASE, error, callable)
        }

        fun greedyString(error: String, callable: Callable<List<String>>): CustomStringArgument {
            return CustomStringArgument(StringType.GREEDY_PHRASE, error, callable)
        }

        fun getString(context: CommandContext<*>, name: String?): String {
            return context.getArgument(name, String::class.java) as String
        }

        fun escapeIfRequired(input: String): String {
            val var1 = input.toCharArray()
            val var2 = var1.size

            for (var3 in 0 until var2) {
                val c = var1[var3]
                if (!StringReader.isAllowedInUnquotedString(c)) {
                    return escape(input)
                }
            }

            return input
        }

        private fun escape(input: String): String {
            val result = StringBuilder("\"")

            for (element in input) {
                if (element == '\\' || element == '"') {
                    result.append('\\')
                }

                result.append(element)
            }

            result.append("\"")
            return result.toString()
        }
    }
}
