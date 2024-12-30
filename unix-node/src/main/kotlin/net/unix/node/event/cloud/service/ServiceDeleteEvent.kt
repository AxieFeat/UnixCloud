package net.unix.node.event.cloud.service

import net.unix.api.service.Service
import net.unix.event.Event

/**
 * These events call on deletion cloud service.
 */
class ServiceDeleteEvent(
    val service: Service
) : Event<ServiceDeleteEvent>()