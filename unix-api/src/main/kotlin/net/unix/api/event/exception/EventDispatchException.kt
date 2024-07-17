package net.unix.api.event.exception

import java.lang.reflect.InvocationTargetException

class EventDispatchException(message: String?, cause: InvocationTargetException?) : RuntimeException(message, cause)
