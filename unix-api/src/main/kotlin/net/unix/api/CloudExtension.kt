package net.unix.api

import net.unix.api.terminal.Color
import java.util.regex.Pattern

/**
 * UnixCloud extensions
 */
object CloudExtension {

    /**
     * Parse args in string.
     *
     * Function replace {NUM} to element from [args] by that index.
     * But index starts from 1
     *
     * Example: "Now {1}".parse("sunny")
     *
     * @param args Elements for replacing
     *
     * @return Changed string
     */
    fun String.parse(vararg args: Any): String {
        val sb = StringBuilder()

        val pattern = Pattern.compile("\\{(\\d+)}")
        val matcher = pattern.matcher(this)

        var pos = 0

        while (matcher.find()) {
            sb.append(this.substring(pos, matcher.start()))

            val index = matcher.group(1).toInt()

            if (index > 0 && index <= args.size) {
                sb.append(args[index - 1])
            }

            pos = matcher.end()
        }

        sb.append(this.substring(pos))

        return sb.toString()
    }

    /**
     * Parse color in string
     *
     * @return Changed string
     */
    fun String.parseColor(): String = Color.translate(this)!!

    /**
     * Strip colors in string
     *
     * @return Changed string
     */
    fun String.stripColor(): String = Color.strip(this)!!

    /**
     * Print object to terminal
     *
     * @return Current instance of [T]
     */
    fun <T> T.print(): T {
        CloudAPI.instance.logger.info(this.toString())

        return this
    }
}