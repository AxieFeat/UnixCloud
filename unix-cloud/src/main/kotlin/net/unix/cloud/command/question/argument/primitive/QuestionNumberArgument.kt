package net.unix.cloud.command.question.argument.primitive

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import java.util.concurrent.CompletableFuture

@Suppress("MemberVisibilityCanBePrivate")
class QuestionNumberArgument(
    val type: Type,
    val min: Number,
    val max: Number
) : QuestionArgument<Number> {

    companion object {
        fun int(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): QuestionNumberArgument =
            QuestionNumberArgument(Type.INT, min, max)

        fun long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE): QuestionNumberArgument =
            QuestionNumberArgument(Type.LONG, min, max)

        fun double(min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE): QuestionNumberArgument =
            QuestionNumberArgument(Type.DOUBLE, min, max)

        fun float(min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE): QuestionNumberArgument =
            QuestionNumberArgument(Type.FLOAT, min, max)
    }

    override fun parse(reader: StringReader): Number {
        when(type) {
            Type.INT -> {
                val result = reader.readString().toIntOrNull() ?:
                    throw QuestionParseException("<red>Waiting int number")

                val min = min.toInt()
                val max = max.toInt()

                if (result !in min..max) {
                    if (result < min) {
                        throw QuestionParseException("<red>Number less then $min")
                    }
                    throw QuestionParseException("<red>Number more then $max")
                }

                return result
            }
            Type.LONG -> {
                val result = reader.readString().toLongOrNull() ?:
                    throw QuestionParseException("<red>Waiting long number")

                val min = min.toLong()
                val max = max.toLong()

                if (result !in min..max) {
                    if (result < min) {
                        throw QuestionParseException("<red>Number less then $min")
                    }
                    throw QuestionParseException("<red>Number more then $max")
                }

                return result
            }
            Type.DOUBLE -> {
                val result = reader.readString().toDoubleOrNull() ?:
                    throw QuestionParseException("<red>Waiting double number")

                val min = min.toDouble()
                val max = max.toDouble()

                if (result !in min..max) {
                    if (result < min) {
                        throw QuestionParseException("<red>Number less then $min")
                    }
                    throw QuestionParseException("<red>Number more then $max")
                }

                return result
            }
            Type.FLOAT -> {
                val result = reader.readString().toFloatOrNull() ?:
                    throw QuestionParseException("<red>Waiting float number")

                val min = min.toFloat()
                val max = max.toFloat()

                if (result !in min..max) {
                    if (result < min) {
                        throw QuestionParseException("<red>Number less then $min")
                    }
                    throw QuestionParseException("<red>Number more then $max")
                }

                return result
            }
        }
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return builder.buildFuture()
    }

    enum class Type {
        INT, LONG, DOUBLE, FLOAT
    }
}