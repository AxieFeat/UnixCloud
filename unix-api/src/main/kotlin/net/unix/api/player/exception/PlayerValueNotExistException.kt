package net.unix.api.player.exception

import net.unix.api.player.CloudPlayer

/**
 * Throw if [CloudPlayer] does not have called field/method
 */
class PlayerValueNotExistException(message: String?, throwable: Throwable?) : RuntimeException(message, throwable)