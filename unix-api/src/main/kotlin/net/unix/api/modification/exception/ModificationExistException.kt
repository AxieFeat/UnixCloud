package net.unix.api.modification.exception

/**
 * If modification with this name already loaded
 */
class ModificationExistException(
    message: String?,
    cause: Throwable?
) : ModificationLoadException(message, cause) {
    constructor(message: String): this(message, null)
    constructor(): this(null, null)
}