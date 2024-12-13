package net.unix.node.event.cloud.service

import net.unix.api.service.CloudService
import net.unix.event.Event

/**
 * These events call on deletion cloud service.
 */
class ServiceDeleteEvent(
    val service: CloudService
) : Event<ServiceDeleteEvent>()