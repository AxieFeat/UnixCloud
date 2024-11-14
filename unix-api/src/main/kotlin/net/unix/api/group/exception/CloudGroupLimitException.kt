package net.unix.api.group.exception

import net.unix.api.group.CloudGroup

/**
 * Throw if [CloudGroup.servicesCount] is more than [CloudGroup.serviceLimit] or
 * [CloudGroup.serviceLimit] less than [CloudGroup.servicesCount].
 */
class CloudGroupLimitException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)