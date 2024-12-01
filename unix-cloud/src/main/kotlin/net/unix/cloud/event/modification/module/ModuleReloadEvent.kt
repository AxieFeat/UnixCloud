package net.unix.cloud.event.modification.module

import net.unix.event.Cancellable
import net.unix.api.modification.module.Module
import net.unix.event.Event

/**
 * Module reload event.
 *
 * @param module Unloaded module.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ModuleReloadEvent(
    val module: Module
) : Event<ModuleReloadEvent>(), Cancellable {

    /**
     * If your cancel event - module will not reload.
     */
    override var cancelled: Boolean = false

}