package net.unix.cloud.terminal.converter

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter
import org.apache.logging.log4j.core.pattern.PatternFormatter
import org.apache.logging.log4j.util.PerformanceSensitive
import org.apache.logging.log4j.util.PropertiesUtil
import java.util.regex.Pattern

@Plugin(name = "colorFormatting", category = "Converter")
@ConverterKeys("colorFormatting")
@PerformanceSensitive("allocation")
open class HexFormattingConverter protected constructor(private val formatters: List<PatternFormatter>) :
    LogEventPatternConverter("formatting", null as String?) {

    override fun format(event: LogEvent, toAppendTo: StringBuilder) {
        val start = toAppendTo.length
        var i = 0

        val size = formatters.size
        while (i < size) {
            formatters[i].format(event, toAppendTo)
            ++i
        }

        if (!KEEP_FORMATTING && toAppendTo.length != start) {
            var content = toAppendTo.substring(start)
            content = convertRGBColors(content)
            format(content, toAppendTo, start)
        }
    }

    companion object {
        private val KEEP_FORMATTING =
            PropertiesUtil.getProperties().getBooleanProperty("terminal.keepMinecraftFormatting")
        private const val ANSI_RESET = "\u001b[m"
        private const val COLOR_CHAR = '§'
        private const val LOOKUP = "0123456789abcdefklmnor"
        private const val RGB_ANSI = "\u001b[38;2;%d;%d;%dm"
        private val NAMED_PATTERN: Pattern = Pattern.compile("§[0-9a-fk-orA-FK-OR]")
        private val RGB_PATTERN: Pattern = Pattern.compile("§x(§[0-9a-fA-F]){6}")
        private val ansiCodes = arrayOf(
            "\u001b[0;30m",
            "\u001b[0;34m",
            "\u001b[0;32m",
            "\u001b[0;36m",
            "\u001b[0;31m",
            "\u001b[0;35m",
            "\u001b[0;33m",
            "\u001b[0;37m",
            "\u001b[0;30;1m",
            "\u001b[0;34;1m",
            "\u001b[0;32;1m",
            "\u001b[0;36;1m",
            "\u001b[0;31;1m",
            "\u001b[0;35;1m",
            "\u001b[0;33;1m",
            "\u001b[0;37;1m",
            "\u001b[5m",
            "\u001b[21m",
            "\u001b[9m",
            "\u001b[4m",
            "\u001b[3m",
            "\u001b[m"
        )

        private fun convertRGBColors(input: String): String {
            val matcher = RGB_PATTERN.matcher(input)
            val buffer = StringBuffer()

            while (matcher.find()) {
                val s = matcher.group().replace('§'.toString(), "").replace('x', '#')
                val hex = Integer.decode(s)
                val red = hex shr 16 and 255
                val green = hex shr 8 and 255
                val blue = hex and 255
                val replacement = String.format("\u001b[38;2;%d;%d;%dm", red, green, blue)
                matcher.appendReplacement(buffer, replacement)
            }

            matcher.appendTail(buffer)
            return buffer.toString()
        }

        private fun stripRGBColors(input: String): String {
            val matcher = RGB_PATTERN.matcher(input)
            val buffer = StringBuffer()

            while (matcher.find()) {
                matcher.appendReplacement(buffer, "")
            }

            matcher.appendTail(buffer)
            return buffer.toString()
        }

        fun format(content: String, result: StringBuilder, start: Int) {
            val next = content.indexOf(167.toChar())
            val last = content.length - 1
            if (next != -1 && next != last) {
                val matcher = NAMED_PATTERN.matcher(content)
                val buffer = StringBuffer()

                while (matcher.find()) {
                    val format = "0123456789abcdefklmnor".indexOf(matcher.group()[1].lowercaseChar())
                    if (format != -1) {
                        matcher.appendReplacement(buffer, ansiCodes[format])
                    }
                }

                matcher.appendTail(buffer)
                result.setLength(start)
                result.append(buffer.toString())

                result.append("\u001b[m")
            } else {
                result.setLength(start)
                result.append(content)

                result.append("\u001b[m")
            }
        }

        fun newInstance(config: Configuration?, options: Array<String?>): HexFormattingConverter? {
            if (options.size in 1..2) {
                if (options[0] == null) {
                    LOGGER.error("No pattern supplied")
                    return null
                } else {
                    val parser = PatternLayout.createPatternParser(config)
                    val formatters = parser.parse(options[0])
                    val strip = options.size > 1 && "strip" == options[1]
                    return HexFormattingConverter(formatters)
                }
            } else {
                LOGGER.error("Incorrect number of options. Expected at least 1, max 2 received " + options.size)
                return null
            }
        }
    }
}