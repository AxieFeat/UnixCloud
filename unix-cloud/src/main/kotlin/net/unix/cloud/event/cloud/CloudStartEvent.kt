package net.unix.cloud.event.cloud

import net.unix.cloud.CloudInstance
import net.unix.event.Event

/**
 * Calls when used [CloudInstance.start].
 */
class CloudStartEvent : Event<CloudStartEvent>()