package net.unix.api.service.exception

import net.unix.api.service.CloudService

/**
 * Throw on try edit deleted [CloudService]
 */
class CloudServiceModificationException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)