package net.unix.cloud.event.cloud

import net.unix.api.CloudBuilder
import net.unix.api.event.Event

/**
 * Calls when UnixCloud start. You can change used classes by [CloudBuilder].
 *
 * @param builder Cloud instance builder. You can change any value.
 */
class CloudStartEvent(
    var builder: CloudBuilder
) : Event<CloudStartEvent>()