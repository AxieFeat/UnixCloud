package net.unix.api.modification

/**
 * Info about some [Modification]
 */
interface ModificationInfo {
    /**
     * Path to [Modification] main class
     */
    val main: String

    /**
     * Modification name
     */
    val name: String

    /**
     * Modification version
     */
    val version: String

    /**
     * Modification description
     */
    val description: String

    /**
     * Modification website
     */
    val website: String

    /**
     * Modification authors
     */
    val authors: List<String>
}