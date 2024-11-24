package net.unix.cloud.event.modification

import net.unix.event.Event
import net.unix.api.modification.Modification

/**
 * General modification event
 */
abstract class ModificationEvent<T>(
    modification: Modification?
) : Event<T>()