package net.unix.api.group.exception

import net.unix.api.group.Group

/**
 * Throw if [Group.servicesCount] is more than [Group.serviceLimit] or
 * [Group.serviceLimit] less than [Group.servicesCount].
 */
class GroupLimitException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)