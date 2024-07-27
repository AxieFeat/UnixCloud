package net.unix.api.command.aether

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import net.kyori.adventure.text.Component
import net.unix.api.CloudAPI
import net.unix.api.CloudExtension.deserializeComponent
import kotlin.math.max

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
        CloudAPI.instance.logger.error(*ex.formatted.toTypedArray())
    }

    /**
     * Get colored and formatted error for printing
     */
    val CommandSyntaxException.formatted: List<Component>
        get() {
            if (input == null || cursor < 0) {
                return listOf()
            }

            val builder = Component.text()
            val length = input.length

            if (length > CommandSyntaxException.CONTEXT_AMOUNT) {
                builder.append(Component.text("..."))
            }

            val context = input.substring(
                max(0.0, (length - CommandSyntaxException.CONTEXT_AMOUNT).toDouble()).toInt(),
                length
            ).split(" ")

            context.forEachIndexed { index, str ->
                if ((index + 1) != context.size) {
                    builder.append(Component.text("$str "))
                } else {
                    builder.append("<red><u>$str</u></red>".deserializeComponent())
                }
            }

            builder.append("<reset><red><i><--[HERE]</i></red>".deserializeComponent())


            return listOf("<red>${this.rawMessage.string}</red>".deserializeComponent(), builder.build())
        }
}