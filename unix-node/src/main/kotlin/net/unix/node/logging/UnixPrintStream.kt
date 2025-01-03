package net.unix.node.logging

import net.unix.node.configuration.UnixConfiguration
import java.io.PrintStream

object UnixPrintStream : PrintStream(System.out) {

    private fun process(x: Any?) {
        if(UnixConfiguration.terminal.logger.warnSTDOUT)
            CloudLogger.warning("<yellow>Do not use STDOUT for printing to console.")

        CloudLogger.info(x.toString())
    }

    override fun print(x: Any?) = process(x)
    override fun print(x: Boolean) = process(x)
    override fun print(x: Char) = process(x)
    override fun print(x: CharArray) = process(x)
    override fun print(x: Double) = process(x)
    override fun print(x: Float) = process(x)
    override fun print(x: Int) = process(x)
    override fun print(x: Long) = process(x)
    override fun print(x: String?) = process(x)

    override fun println(x: Any?) = process(x)
    override fun println(x: Boolean) = process(x)
    override fun println(x: Char) = process(x)
    override fun println() = process(" ")
    override fun println(x: CharArray) = process(x)
    override fun println(x: Double) = process(x)
    override fun println(x: Float) = process(x)
    override fun println(x: Int) = process(x)
    override fun println(x: Long) = process(x)
    override fun println(x: String?) = process(x)
}