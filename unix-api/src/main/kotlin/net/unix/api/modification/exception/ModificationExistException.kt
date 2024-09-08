package net.unix.api.modification.exception

/**
 * If modification with this name already loaded.
 */
class ModificationExistException(
    message: String? = null,
    cause: Throwable? = null
) : ModificationLoadException(message, cause)