package net.unix.event

/**
 * A typed event occurs in different versions (e.g. pre- and post-action events) and can be listened to seperatly.
 */
interface Typed {

    /**
     * The event type is provided as an integer value. A listener method can specify which type of event it wants to
     * listen to.
     *
     * @return The event type identifier.
     */
    val type: Int

}