package net.unix.api.event.impl.cloud

import net.unix.api.event.Event

/**
 * Calls when UnixCloud stopping
 */
class CloudShutdownEvent : Event<CloudStartEvent>()