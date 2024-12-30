package net.unix.node.event.cloud.group

import net.unix.api.group.Group
import net.unix.event.Event

/**
 * These events call on creation new instance of group.
 */
class GroupCreateEvent(
    val group: Group
) : Event<GroupCreateEvent>()