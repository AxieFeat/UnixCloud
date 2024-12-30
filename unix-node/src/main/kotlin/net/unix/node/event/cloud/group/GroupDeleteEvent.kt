package net.unix.node.event.cloud.group

import net.unix.api.group.Group
import net.unix.event.Event

/**
 * These events call on deletion cloud group.
 */
class GroupDeleteEvent(
    val group: Group
) : Event<GroupDeleteEvent>()