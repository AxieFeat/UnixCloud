package net.unix.api.modification.exception

/**
 * Exception on modification load
 */
open class ModificationLoadException(
    message: String?,
    cause: Throwable?
) : RuntimeException(message, cause) {
    constructor(message: String): this(message, null)
    constructor(): this(null, null)
}