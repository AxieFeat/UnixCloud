package net.unix.event

import net.unix.event.listener.EventHandler

/**
 * Registers and manages listeners and handles event dispatching.
 */
interface EventManager {

    /**
     * Identify and registers all [EventHandler] methods in the class of given Object instance. The
     * instance will be saved, because it is used to invoke the listeners, thus the listeners cannot be static and the object won't get
     * destroyed by garbage collection. Use it to unregister the listeners.
     *
     * @param listenerClassInstance An object of a class containing event handlers.
     */
    fun registerListeners(listenerClassInstance: Any)

    /**
     * Unregisters all event handlers associated with this object.
     *
     * @param listenerClassInstance An object of a class containing even handlers that has been registered at the event handler.
     */
    fun unregisterListeners(listenerClassInstance: Any)

    /**
     * Unregisters all event handlers associated with given event type.
     *
     * @param eventClass class of the event.
     */
    fun unregisterListenersOfEvent(eventClass: Class<out Event<*>>)

    /**
     * Dispatch an event by calling any listener responsible for the given event.
     *
     * @param event An arbitrary instance of any subclass of [Event].
     */
    fun callEvent(event: Event<*>)
}