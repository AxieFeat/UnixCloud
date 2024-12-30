package net.unix.api.service.exception

import net.unix.api.service.Service

/**
 * Throw on try edit deleted [Service].
 */
class ServiceModificationException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)