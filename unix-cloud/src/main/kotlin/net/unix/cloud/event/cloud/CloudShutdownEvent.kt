package net.unix.cloud.event.cloud

import net.unix.event.Event

/**
 * Calls when UnixCloud stopping.
 */
class CloudShutdownEvent : Event<CloudStartEvent>()