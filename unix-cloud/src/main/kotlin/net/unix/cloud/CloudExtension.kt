package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.CloudExtension
import net.unix.api.terminal.Color
import java.util.regex.Pattern

object CloudExtension : CloudExtension {
    override fun String.parse(vararg args: Any): String {
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

    override fun String.parseColor(): String = Color.translate(this)!!

   override  fun String.stripColor(): String = Color.strip(this)!!

    override fun <T> T.print(): T {
        CloudAPI.instance.logger.info(this.toString())

        return this
    }
}