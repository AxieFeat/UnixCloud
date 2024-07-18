package net.unix.api.command.aether

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import net.unix.api.CloudAPI
import net.unix.api.scheduler.Scheduler.scheduler
import kotlin.math.max
import kotlin.math.min

object SyntaxExceptionBuilder {

    private val dynamic2CommandExceptionType = Dynamic2CommandExceptionType { _: Any?, _: Any? ->
        LiteralMessage(
            ""
        )
    }

    /**
     * Create instance of [CommandSyntaxException]
     *
     * @param message Error message
     * @param reader String reader from parser
     *
     * @return Instance of [CommandSyntaxException]
     */
    fun exception(message: String, reader: StringReader): CommandSyntaxException {
        return CommandSyntaxException(dynamic2CommandExceptionType, LiteralMessage(message), reader.string, reader.cursor)
    }

    /**
     * Create instance of [CommandSyntaxException]
     *
     * @param message Error message
     *
     * @return Instance of [CommandSyntaxException]
     */
    fun exception(message: String): CommandSyntaxException {
        return CommandSyntaxException(dynamic2CommandExceptionType, LiteralMessage(message))
    }

    /**
     * Print syntax exception to terminal
     *
     * @param ex Instance of [CommandSyntaxException]
     */
    fun print(ex: CommandSyntaxException) {
        CloudAPI.instance.logger.error(*ex.colored.toTypedArray())
    }

    /**
     * Get colored and formatted error for printing
     */
    val CommandSyntaxException.colored: List<String>
        get() {
            if (input == null || cursor < 0) {
                return listOf()
            }

            val builder = StringBuilder()
            val cursor = min(input.length.toDouble(), cursor.toDouble()).toInt()

            if (cursor > CommandSyntaxException.CONTEXT_AMOUNT) {
                builder.append("...")
            }

            val context = input.substring(
                max(0.0, (cursor - CommandSyntaxException.CONTEXT_AMOUNT).toDouble())
                    .toInt(), cursor
            ).split(" ")

            if (context.size == 1) {
                builder.append("&c&n${context[0]}&r")
            } else {
                builder
                    .append(context[0] + " ")
                    .append("&c&n${context[1]}&r")
            }

            builder.append("&r&c&o<--[HERE]")


            return listOf("&c${this.rawMessage.string}&r", builder.toString())
        }
}