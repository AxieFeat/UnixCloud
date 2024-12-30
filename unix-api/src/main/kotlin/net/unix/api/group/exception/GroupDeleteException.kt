package net.unix.api.group.exception

import net.unix.api.group.SaveableGroup

/**
 * Throw if used [SaveableGroup.delete] with active cloud services.
 */
class GroupDeleteException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)