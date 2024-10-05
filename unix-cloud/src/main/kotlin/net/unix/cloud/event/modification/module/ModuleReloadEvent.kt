package net.unix.cloud.event.modification.module

import net.unix.api.event.Cancellable
import net.unix.api.modification.module.Module
import net.unix.cloud.event.modification.ModificationEvent

/**
 * Module reload event.
 *
 * @param module Unloaded module.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ModuleReloadEvent(
    val module: Module
) : ModificationEvent<ModuleReloadEvent>(module), Cancellable {

    /**
     * If your cancel event - module will not reload.
     */
    override var cancelled: Boolean = false

}