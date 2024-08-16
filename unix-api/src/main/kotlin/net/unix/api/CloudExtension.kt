package net.unix.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.unix.api.CloudExtension.rem
import net.unix.api.terminal.Color.Companion.stripColor
import java.security.MessageDigest
import java.util.Base64
import java.util.regex.Pattern

/**
 * UnixCloud extensions
 */
object CloudExtension {

    private val sha256 = MessageDigest.getInstance("SHA-256")
    private val md5 = MessageDigest.getInstance("MD5")
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
     * Hash of string
     *
     * SHA-256 -> MD5 -> Base64 -> SHA-256
     *
     * @return Hash result
     */
    fun String.hash(): String {
        val bytes = this.toByteArray()

        val first = sha256.digest(bytes).fold("") { str, it -> str + "%02x".format(it) }.toByteArray()
        val second = md5.digest(first).fold("") { str, it -> str + "%02x".format(it) }.toByteArray()
        
        return Base64.getEncoder().encode(second).fold("") { str, it -> str + "%02x".format(it) }
    }

    /**
     * Abbreviation of the if statement for Boolean
     *
     * Example:
     *
     * val result = true % "312" or "123"
     */
    inline infix operator fun <reified T> Boolean.rem(any: T): BoolInterface<T> {
        val boolFunction = object : BoolInterface<T> {
            override val from: Boolean = this@rem
            override val result: T = any

        }

        return boolFunction
    }

    /**
     * @see [rem]
     */
    inline infix fun <reified T> BoolInterface<T>.or(any: T): T {
        val result = this.from!!

        val boolFunction = object : BoolInterface<T> {
            override val from: Boolean? = null
            override val result: T = any
        }

        return if (result) this.result else boolFunction.result
    }

    interface BoolInterface<T> {
        val from: Boolean?
        val result: T
    }
}