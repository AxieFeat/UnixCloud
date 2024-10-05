package net.unix.cloud.event.modification.extension

import net.unix.api.event.Cancellable
import net.unix.api.modification.module.Module
import net.unix.cloud.event.modification.ModificationEvent

/**
 * Extension load event.
 *
 * @param extension Loaded Extension. Null if caught error.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ExtensionLoadEvent(
    val extension: Module?
) : ModificationEvent<ExtensionLoadEvent>(extension), Cancellable {

    /**
     * If your cancel event - extension will not load.
     */
    override var cancelled: Boolean = false

}