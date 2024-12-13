package net.unix.node.event.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.event.Event

/**
 * These events call on deletion cloud group.
 */
class GroupDeleteEvent(
    val group: CloudGroup
) : Event<GroupDeleteEvent>()