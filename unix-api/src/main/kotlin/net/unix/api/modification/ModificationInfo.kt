package net.unix.api.modification

import net.unix.api.pattern.Serializable
import net.unix.api.pattern.Nameable

/**
 * Info about some [Modification].
 */
interface ModificationInfo : Serializable, Nameable {

    /**
     * Path to [Modification] main class.
     */
    val main: String

    /**
     * Modification name.
     */
    override val name: String

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