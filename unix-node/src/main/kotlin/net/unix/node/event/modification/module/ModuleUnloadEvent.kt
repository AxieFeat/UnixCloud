package net.unix.node.event.modification.module

import net.unix.event.Cancellable
import net.unix.api.modification.module.Module
import net.unix.event.Event

/**
 * Module unload event.
 *
 * @param module Unloaded module.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ModuleUnloadEvent(
    val module: Module
) : Event<ModuleUnloadEvent>(), Cancellable {

    /**
     * If your cancel event - module will not unload.
     */
    override var cancelled: Boolean = false

}