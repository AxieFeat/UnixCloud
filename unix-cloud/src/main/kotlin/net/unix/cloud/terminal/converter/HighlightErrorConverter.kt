package net.unix.cloud.terminal.converter

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter
import org.apache.logging.log4j.core.pattern.PatternFormatter
import org.apache.logging.log4j.util.PerformanceSensitive

@Plugin(name = "highlightError", category = "Converter")
@ConverterKeys("highlightError")
@PerformanceSensitive("allocation")
class HighlightErrorConverter protected constructor(private val formatters: List<PatternFormatter>) :
    LogEventPatternConverter("highlightError", null as String?) {
    override fun format(event: LogEvent, toAppendTo: StringBuilder) {

        val level = event.level
        if (level.isMoreSpecificThan(Level.ERROR)) {
            this.format("\u001b[31;1m", event, toAppendTo)
            return
        }

        if (level.isMoreSpecificThan(Level.WARN)) {
            this.format("\u001b[33;1m", event, toAppendTo)
            return
        }

        var i = 0

        val size = formatters.size
        while (i < size) {
            formatters[i].format(event, toAppendTo)
            ++i
        }
    }

    private fun format(style: String, event: LogEvent, toAppendTo: StringBuilder) {
        val start = toAppendTo.length
        toAppendTo.append(style)
        val end = toAppendTo.length
        var i = 0

        val size = formatters.size
        while (i < size) {
            formatters[i].format(event, toAppendTo)
            ++i
        }

        if (toAppendTo.length == end) {
            toAppendTo.setLength(start)
        } else {
            toAppendTo.append("\u001b[m")
        }
    }

    override fun handlesThrowable(): Boolean {
        val var1: Iterator<*> = formatters.iterator()

        var formatter: PatternFormatter
        do {
            if (!var1.hasNext()) {
                return false
            }

            formatter = var1.next() as PatternFormatter
        } while (!formatter.handlesThrowable())

        return true
    }

    companion object {
        private const val ANSI_RESET = "\u001b[m"
        private const val ANSI_ERROR = "\u001b[31;1m"
        private const val ANSI_WARN = "\u001b[33;1m"
        fun newInstance(config: Configuration?, options: Array<String?>): HighlightErrorConverter? {
            if (options.size != 1) {
                LOGGER.error("Incorrect number of options on highlightError. Expected 1 received " + options.size)
                return null
            } else if (options[0] == null) {
                LOGGER.error("No pattern supplied on highlightError")
                return null
            } else {
                val parser = PatternLayout.createPatternParser(config)
                val formatters = parser.parse(options[0])
                return HighlightErrorConverter(formatters)
            }
        }
    }
}