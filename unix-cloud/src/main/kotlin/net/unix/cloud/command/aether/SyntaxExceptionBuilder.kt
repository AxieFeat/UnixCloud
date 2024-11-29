@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.cloud.command.aether

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import net.kyori.adventure.text.Component
import net.unix.cloud.CloudExtension.deserializeComponent
import net.unix.cloud.logging.CloudLogger
import org.koin.core.component.KoinComponent
import kotlin.math.max

object SyntaxExceptionBuilder : KoinComponent {

    private val hereMessage: Component = "<reset><red><i><--[HERE]</i></red>".deserializeComponent()

    private val dynamic2CommandExceptionType = Dynamic2CommandExceptionType { _: Any?, _: Any? ->
        LiteralMessage(
            ""
        )
    }

    /**
     * Create instance of [CommandSyntaxException].
     *
     * @param message Error message.
     * @param reader String reader from parser.
     *
     * @return Instance of [CommandSyntaxException].
     */
    fun exception(message: String, reader: StringReader): CommandSyntaxException {
        return CommandSyntaxException(dynamic2CommandExceptionType, LiteralMessage(message), reader.string, reader.cursor)
    }

    /**
     * Create instance of [CommandSyntaxException].
     *
     * @param message Error message.
     *
     * @return Instance of [CommandSyntaxException].
     */
    fun exception(message: String): CommandSyntaxException {
        return CommandSyntaxException(dynamic2CommandExceptionType, LiteralMessage(message))
    }

    /**
     * Print syntax exception to terminal.
     *
     * @param ex Instance of [CommandSyntaxException].
     */
    fun print(ex: CommandSyntaxException) {
        ex.formatted.toTypedArray().forEach { CloudLogger.severe(it) }
    }

    /**
     * Get colored and formatted error for printing.
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

            builder.append(hereMessage)

            return listOf("<red>${this.rawMessage.string}</red>".deserializeComponent(), builder.build())
        }
}