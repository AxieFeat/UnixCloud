package net.unix.api.event

/**
 * A typed event occurs in different versions (e.g. pre- and post-action events) and can be listened to seperatly.
 */
interface Typed {
    /**
     * The event type is provided as a integer value. A listener method can specify which type of event it want to
     * listen to
     *
     * @return The event type identifier
     */
    val type: Int
}