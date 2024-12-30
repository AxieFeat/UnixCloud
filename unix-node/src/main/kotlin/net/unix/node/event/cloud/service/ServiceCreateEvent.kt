package net.unix.node.event.cloud.service

import net.unix.api.service.Service
import net.unix.event.Event

/**
 * These events call on creation new instance of service.
 */
class ServiceCreateEvent(
    val service: Service
) : Event<ServiceCreateEvent>()