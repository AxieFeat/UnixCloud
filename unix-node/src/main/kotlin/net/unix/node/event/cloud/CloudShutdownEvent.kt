package net.unix.node.event.cloud

import net.unix.event.Event

/**
 * Calls when UnixCloud stopping.
 */
class CloudShutdownEvent : Event<CloudStartEvent>()