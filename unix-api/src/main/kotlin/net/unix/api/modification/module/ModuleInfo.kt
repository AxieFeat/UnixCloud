package net.unix.api.modification.module

import net.unix.api.modification.ModificationInfo

/**
 * Info about [CloudModule]
 */
interface ModuleInfo : ModificationInfo {

    companion object

    /**
     * Required dependencies
     */
    val depends: List<String>

    /**
     * Optional dependencies
     */
    val soft: List<String>
}