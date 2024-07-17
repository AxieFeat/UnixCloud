package net.unix.api.event

/**
 * An event that can be called and will then be send to the listeners, so they can react to it.
 *
 * @param T Event class object type
 */
@Suppress("UNCHECKED_CAST")
abstract class Event<T> {

    /**
     * Call event
     *
     * @return Instance of [T]
     */
    fun callEvent(): T {
        EventManager.callEvent(this)

        return this as T
    }
}
