package net.unix.node.event.modification.module

import net.unix.event.Cancellable
import net.unix.api.modification.module.Module
import net.unix.event.Event

/**
 * Module load event.
 *
 * @param module Loaded module. Null if caught error.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ModuleLoadEvent(
    val module: Module?
) : Event<ModuleLoadEvent>(), Cancellable {

    /**
     * If your cancel event - module will not load.
     */
    override var cancelled: Boolean = false

}