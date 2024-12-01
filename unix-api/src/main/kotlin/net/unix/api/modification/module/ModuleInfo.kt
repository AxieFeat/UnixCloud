package net.unix.api.modification.module

import net.unix.api.modification.ModificationInfo
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * Info about [Module].
 */
interface ModuleInfo : ModificationInfo {

    /**
     * Required dependencies.
     */
    @get:Throws(RemoteException::class)
    val depends: List<String>

    /**
     * Optional dependencies.
     */
    @get:Throws(RemoteException::class)
    val soft: List<String>

    companion object

}