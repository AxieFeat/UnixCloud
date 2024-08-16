package net.unix.api.modification.exception

/**
 * Exception on modification load
 */
open class ModificationLoadException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)