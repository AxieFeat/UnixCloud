package net.unix.event.exception

import java.lang.reflect.InvocationTargetException

/**
 * Throw on event dispatching exception.
 */
class EventDispatchException(
    message: String? = null,
    cause: InvocationTargetException? = null
) : RuntimeException(message, cause)
