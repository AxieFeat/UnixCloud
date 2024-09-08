package net.unix.api.event.exception

import java.lang.reflect.InvocationTargetException

/**
 * Throw on event dispatching exception.
 */
class EventDispatchException(message: String?, cause: InvocationTargetException?) : RuntimeException(message, cause)
