package net.unix.api.event.exception

/**
 * This policy sets the error handling behaviour of dispatched events.
 */
enum class ErrorPolicy {

    /**
     * Throw a [EventDispatchException] on errors in dispatched events.
     */
    EXCEPTION,

    /**
     * Output error only in log.
     */
    LOG

}