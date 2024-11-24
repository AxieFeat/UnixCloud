package net.unix.cloud.event.modification.module

import net.unix.event.Cancellable
import net.unix.api.modification.module.Module
import net.unix.cloud.event.modification.ModificationEvent

/**
 * Module unload event.
 *
 * @param module Unloaded module.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ModuleUnloadEvent(
    val module: Module
) : ModificationEvent<ModuleUnloadEvent>(module), Cancellable {

    /**
     * If your cancel event - module will not unload.
     */
    override var cancelled: Boolean = false

}