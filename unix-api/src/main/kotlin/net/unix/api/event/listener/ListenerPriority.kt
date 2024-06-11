package net.unix.api.event.listener

/**
 * Приоритеты слушателей событий. Самый высокий приоритет вызывается первым
 */
enum class ListenerPriority {
    HIGHEST, HIGH, NORMAL, LOW, LOWEST
}