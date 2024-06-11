package net.unix.api.event.exception

/**
 * Эта "политика" задает поведение обработки ошибок в отправляемых событиях.
 */
enum class ErrorPolicy {
    /**
     * [EventDispatchException] При ошибках в обработке события
     */
    EXCEPTION,

    /**
     * Вывод ошибок только в лог
     */
    LOG
}