package net.unix.api.modification.module

import net.unix.api.modification.ModificationInfo

/**
 * Info about [Module].
 */
interface ModuleInfo : ModificationInfo {

    /**
     * Required dependencies.
     */
    val depends: List<String>

    /**
     * Optional dependencies.
     */
    val soft: List<String>

    companion object

}