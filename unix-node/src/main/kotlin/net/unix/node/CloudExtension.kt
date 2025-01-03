@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION")

package net.unix.node

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.unix.api.service.Service
import net.unix.node.CloudExtension.rem
import net.unix.api.terminal.Color.Companion.stripColor
import net.unix.node.logging.CloudLogger
import java.io.File
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Pattern

/**
 * UnixCloud extensions.
 */
object CloudExtension {

    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val miniMessage = MiniMessage.builder().build()
    private val ansiSerializer = ANSIComponentSerializer.builder().build()
    private val legacySerializer = LegacyComponentSerializer.builder().character('&').hexColors().build()
    private val formatPattern = Pattern.compile("\\{(\\d+)}")
    private val usedUUID = mutableListOf<UUID>()

    /**
     * Format args in string.
     *
     * Function replace {NUM} to element from [args] by that index.
     * But index starts from 1.
     *
     * Example: "Now {1}".format("sunny")
     *
     * @param args Elements for replacing.
     *
     * @return Changed string.
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
                    sb.append(arg.toString())
                }
            }

            pos = matcher.end()
        }

        sb.append(this.substring(pos))

        return sb.toString()
    }

    /**
     * Format args in [Component].
     *
     * @see [format]
     */
    fun Component.format(vararg args: Any): Component {
        return this.serialize().format(args).deserializeComponent()
    }

    /**
     * Replace text in [Component].
     *
     * @param old Replaceable text.
     * @param new Text to replace.
     *
     * @return New instance of [Component].
     */
    fun Component.replace(old: String, new: String): Component {
        return this.serialize().replace(old, new).deserializeComponent()
    }

    /**
     * Strip colors (...and other) in [Component].
     *
     * @return Text without any formatting.
     */
    fun Component.strip(): String {
        return this.serializeLegacy().stripColor()
    }

    /**
     * Deserialize [Component] from string by [MiniMessage].
     *
     * @return Deserialized [Component].
     */
    fun String.deserializeComponent(): Component {
        return miniMessage.deserialize(this)
    }

    /**
     * Serialize [Component] to string by [MiniMessage].
     *
     * @return Serialized [Component].
     */
    fun Component.serialize(): String {
        return miniMessage.serialize(this)
    }

    /**
     * Serialize [Component] to ansi.
     *
     * @return String with ansi colors.
     */
    fun Component.serializeAnsi(): String {
        return ansiSerializer.serialize(this)
    }

    /**
     * Serialize [Component] to legacy.
     *
     * @return Legacy colored string.
     */
    fun Component.serializeLegacy(): String {
        return legacySerializer.serialize(this)
    }

    /**
     * Print object to terminal.
     *
     * @return Current instance of [T].
     */
    fun <T> T.print(): T {
        CloudLogger.info(this.toString())

        return this
    }

    /**
     * Deserialize json from [JarEntry].
     *
     * @param T Type for deserializing.
     * @param entry Jar entry.
     *
     * @return Deserialized [T].
     */
    fun <T> JarFile.deserializeJson(entry: JarEntry): T {
        val reader = this.getInputStream(entry).reader()

        val result = gson.fromJson<T>(reader, MutableMap::class.java)

        reader.close()
        this.close()

        return result
    }

    /**
     * Read JSON from file.
     *
     * @param T Type of object.
     *
     * @return Deserialize result.
     */
    inline fun <reified T> File.readJson(): T {
        val text = this.readText()

        return text.readJson()
    }

    /**
     * Read JSON from string.
     *
     * @param T Type of object.
     *
     * @return Deserialize result.
     */
    inline fun <reified T> String.readJson(): T {
        val result = gson.fromJson(this, T::class.java)

        return result
    }

    fun Any.toJson(file: File) {
        val text = this.toJson()

        file.writeText(text)
    }

    fun Any.toJson(): String {
        return gson.toJson(this)
    }

    /**
     * Generate unique UUID for [Service]. It'll be unique by current session.
     *
     * @return Unique UUID.
     */
    fun uniqueUUID(): UUID {
        val random = UUID.randomUUID()

        if (usedUUID.contains(random)) return uniqueUUID() else usedUUID.add(random)

        return random
    }

    /**
     * Is uuid in use.
     *
     * @return Is uuid in use.
     */
    fun UUID.inUse(): Boolean = usedUUID.contains(this)

    /**
     * Abbreviation of the if statement for Boolean.
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

    /**
     * Don't use this, created for [rem]
     *
     * @see [rem]
     */
    interface BoolInterface<T> {
        val from: Boolean?
        val result: T
    }
}