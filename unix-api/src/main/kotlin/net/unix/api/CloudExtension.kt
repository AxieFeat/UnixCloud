package net.unix.api

/**
 * UnixCloud extensions
 */
interface CloudExtension {

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
    fun String.parse(vararg args: Any): String

    /**
     * Parse color in string
     *
     * @return Changed string
     */
    fun String.parseColor(): String

    /**
     * Strip colors in string
     *
     * @return Changed string
     */
    fun String.stripColor(): String

    /**
     * Print object to terminal
     *
     * @return Current instance of [T]
     */
    fun <T> T.print(): T
}