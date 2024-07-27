package net.unix.api

import com.sun.org.apache.xpath.internal.operations.Bool
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.unix.api.terminal.Color.Companion.stripColor
import java.util.concurrent.Callable
import java.util.regex.Pattern

/**
 * UnixCloud extensions
 */
object CloudExtension {

    private val miniMessage = MiniMessage.builder().build()
    private val ansiSerializer = ANSIComponentSerializer.builder().build()
    private val legacySerializer = LegacyComponentSerializer.builder().character('&').hexColors().build()
    private val formatPattern = Pattern.compile("\\{(\\d+)}")

    /**
     * Format args in string.
     *
     * Function replace {NUM} to element from [args] by that index.
     * But index starts from 1
     *
     * Example: "Now {1}".format("sunny")
     *
     * @param args Elements for replacing
     *
     * @return Changed string
     */
    fun String.format(vararg args: Any): String {
        val sb = StringBuilder()

        val matcher = formatPattern.matcher(this)

        var pos = 0

        while (matcher.find()) {
            sb.append(this.substring(pos, matcher.start()))

            val index = matcher.group(1).toInt()

            if (index > 0 && index <= args.size) {
                val arg = args[index - 1]

                if (arg is Component) {
                    sb.append(arg.serialize())
                } else {
                    sb.append(arg)
                }
            }

            pos = matcher.end()
        }

        sb.append(this.substring(pos))

        return sb.toString()
    }

    /**
     * Format args in [Component]
     *
     * @see [format]
     */
    fun Component.format(vararg args: Any): Component {
        return this.serialize().format(args).deserializeComponent()
    }

    /**
     * Replace text in [Component]
     *
     * @param old Replaceable text
     * @param new Text to replace
     *
     * @return New instance of [Component]
     */
    fun Component.replace(old: String, new: String): Component {
        return this.serialize().replace(old, new).deserializeComponent()
    }

    /**
     * Strip colors (...and other) in [Component]
     *
     * @return Text without any formatting
     */
    fun Component.strip(): String {
        return this.serializeLegacy().stripColor()
    }

    /**
     * Deserialize [Component] from string by [MiniMessage]
     *
     * @return Deserialized [Component]
     */
    fun String.deserializeComponent(): Component {
        return miniMessage.deserialize(this)
    }

    /**
     * Serialize [Component] to string by [MiniMessage]
     *
     * @return Serialized [Component]
     */
    fun Component.serialize(): String {
        return miniMessage.serialize(this)
    }

    /**
     * Serialize [Component] to ansi
     *
     * @return String with ansi colors
     */
    fun Component.serializeAnsi(): String {
        return ansiSerializer.serialize(this)
    }

    /**
     * Serialize [Component] to legacy
     *
     * @return Legacy colored string
     */
    fun Component.serializeLegacy(): String {
        return legacySerializer.serialize(this)
    }

    /**
     * Print object to terminal
     *
     * @return Current instance of [T]
     */
    fun <T> T.print(): T {
        CloudAPI.instance.logger.info(this.toString())

        return this
    }

    /**
     * Abbreviation of the if statement for Boolean
     *
     * Example:
     *
     * val result = true % "312" or "123"
     */
    infix operator fun <T> Boolean.rem(any: T): BoolFunction<T> {
        val boolFunction = BoolFunction.create(this, result = any)

        return boolFunction
    }

    /**
     * @see [rem]
     */
    infix fun <T> BoolFunction<T>.or(any: T): T {
        val result = this.from!!

        val boolFunction = BoolFunction.create(result = any)

        return if (result) this.run() else boolFunction.run()
    }

    abstract class BoolFunction<T>(val from: Boolean? = null) {

        companion object {
            fun <T> create(from: Boolean? = null, result: T): BoolFunction<T> {
                return object : BoolFunction<T>(from) {
                    override fun run(): T {
                        return result
                    }
                }
            }
        }

        abstract fun run(): T
    }
}