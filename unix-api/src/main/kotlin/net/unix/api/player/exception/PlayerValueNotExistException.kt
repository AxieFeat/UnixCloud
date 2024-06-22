package net.unix.api.player.exception

/**
 * Вызывается, если поле/метод в вызываемом объекте не найдено
 */
class PlayerValueNotExistException(message: String?, throwable: Throwable?) : RuntimeException(message, throwable)