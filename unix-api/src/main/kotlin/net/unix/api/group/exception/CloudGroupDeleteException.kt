package net.unix.api.group.exception

import net.unix.api.group.SavableCloudGroup

/**
 * Throw if used [SavableCloudGroup.delete] with active cloud services.
 */
class CloudGroupDeleteException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)