package net.unix.node.event.modification.extension

import net.unix.event.Cancellable
import net.unix.api.modification.extension.Extension
import net.unix.event.Event

/**
 * Extension load event.
 *
 * @param extension Loaded Extension. Null if caught error.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ExtensionLoadEvent(
    val extension: Extension?
) : Event<ExtensionLoadEvent>(), Cancellable {

    /**
     * If your cancel event - extension will not load.
     */
    override var cancelled: Boolean = false

}