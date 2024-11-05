package net.unix.api.modification

import net.unix.api.Serializable

/**
 * Info about some [Modification].
 */
interface ModificationInfo : Serializable {

    /**
     * Path to [Modification] main class.
     */
    val main: String

    /**
     * Modification name.
     */
    val name: String

    /**
     * Modification version.
     */
    val version: String

    /**
     * Modification description.
     */
    val description: String

    /**
     * Modification website.
     */
    val website: String

    /**
     * Modification authors.
     */
    val authors: List<String>

    companion object

}