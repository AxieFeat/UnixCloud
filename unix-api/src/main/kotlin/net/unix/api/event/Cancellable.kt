package net.unix.api.event

/**
 * Use with [Event] for cancellable events.
 */
interface Cancellable {
    /**
     * Whether the event was cancelled.
     *
     * If cancelled = true:
     * A cancelled event will be handed to all listeners although it has been cancelled before. Other listeners can undo
     * the cancel operation.
     */
    var cancelled: Boolean
}