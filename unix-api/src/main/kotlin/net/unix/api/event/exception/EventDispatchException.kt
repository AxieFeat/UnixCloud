package net.unix.api.event.exception

import java.lang.reflect.InvocationTargetException

/**
 * Это исключение выбрасывается если [ErrorPolicy] установлено на [ErrorPolicy.EXCEPTION]
 */
class EventDispatchException(message: String?, cause: InvocationTargetException?) : RuntimeException(message, cause)
