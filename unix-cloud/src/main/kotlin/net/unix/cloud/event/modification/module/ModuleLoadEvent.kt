package net.unix.cloud.event.modification.module

import net.unix.api.event.Cancellable
import net.unix.api.modification.module.Module
import net.unix.cloud.event.modification.ModificationEvent

/**
 * Module load event.
 *
 * @param module Loaded module. Null if caught error.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ModuleLoadEvent(
    val module: Module?
) : ModificationEvent<ModuleLoadEvent>(module), Cancellable {

    /**
     * If your cancel event - module will not load.
     */
    override var cancelled: Boolean = false

}