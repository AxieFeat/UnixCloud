@file:Suppress("UNCHECKED_CAST")

package net.unix.api.event

import net.unix.api.event.exception.ErrorPolicy
import net.unix.api.event.exception.EventDispatchException
import net.unix.api.event.listener.EventHandler
import net.unix.api.event.listener.EventType
import net.unix.api.event.listener.ListenerPriority
import net.unix.api.event.listener.ListenerScope
import net.unix.api.event.scope.ScopeGroup
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Registers and manages listeners and handles event dispatching.
 */
object EventManager {

    //private val logger = CloudAPI.instance.logger

    /**
     * The error policy determines how exceptions on dispatched events will be handled.
     */
    private var ERROR_POLICY: ErrorPolicy = ErrorPolicy.LOG

    private val registeredListeners: MutableMap<Class<out Event<*>?>, CopyOnWriteArrayList<Listener>> = mutableMapOf()
    private const val GLOBAL_SCOPE = "*"

    /**
     * Identify and registers all [EventHandler] methods in the class of given Object instance. The
     * instance will be saved, because it is used to invoke the listeners, thus the listeners cannot be static and the object won't get
     * destroyed by garbage collection. Use it to unregister the listeners.
     *
     * @param listenerClassInstance An object of a class containing event handlers.
     */
    fun registerListeners(listenerClassInstance: Any) {
        for (method: Method in listenerClassInstance.javaClass.methods) {
            if (!method.isAnnotationPresent(EventHandler::class.java)) {
                continue
            }


            // illegal parameter count
            if (method.parameterCount != 1) {
//                logger.error("Ignoring illegal event handler: " + method.name +
//                        ": Wrong number of arguments (required: 1)")
                continue
            }


            // illegal parameter
            if (!Event::class.java.isAssignableFrom(method.parameterTypes[0])) {
//                logger.error(
//                    ("Ignoring illegal event handler: " + method.name + ": Argument must extend " +
//                            Event::class.java.getName())
//                )
                continue
            }

            val eventType: Class<out Event<*>?> =
                method.parameterTypes[0] as Class<out Event<*>?>

            var scope: String = GLOBAL_SCOPE
            if (method.isAnnotationPresent(ListenerScope::class.java)) {
                if (!Scoped::class.java.isAssignableFrom(eventType)) {
//                    logger.error(
//                        ("Ignoring illegal event handler: " + method.name +
//                                ": Handler is scoped, but event not.")
//                    )
                    continue
                }

                scope = method.getAnnotation(ListenerScope::class.java).value
            }

            var listenedEventType = -1
            if (method.isAnnotationPresent(EventType::class.java)) {
                if (!Typed::class.java.isAssignableFrom(eventType)) {
//                    logger.error(
//                        ("Ignoring illegal event handler: " + method.name +
//                                ": Handler is typed, but event not.")
//                    )
                    continue
                }

                listenedEventType = method.getAnnotation(EventType::class.java).value
            }

            val priority = method.getAnnotation(EventHandler::class.java).priority
            val scopeGroup = ScopeGroup(scope)

            val listener = Listener(listenerClassInstance, method, scopeGroup, priority, listenedEventType)
            addListener(eventType, listener)
        }
    }

    /**
     * Adds a listener to the map of registered listeners.
     *
     * @param eventType the event type that listener should handle.
     * @param listener  the listener method.
     */
    private fun addListener(eventType: Class<out Event<*>?>, listener: Listener) {
        if (!registeredListeners.containsKey(eventType)) registeredListeners[eventType] =
            CopyOnWriteArrayList()

        registeredListeners[eventType]!!.add(listener)
    }

    /**
     * Unregisters all event handlers associated with this object.
     *
     * @param listenerClassInstance An object of a class containing even handlers that has been registered at the event handler.
     */
    fun unregisterListeners(listenerClassInstance: Any) {
        registeredListeners.values.forEach { listenerList ->
            var i = 0
            while (i < listenerList.size) {
                if (listenerList[i].listenerClassInstance === listenerClassInstance) {
                    listenerList.removeAt(i)
                    i -= 1
                }
                i++
            }
        }
    }

    /**
     * Unregisters all event handlers associated with given event type.
     *
     * @param eventClass class of the event.
     */
    fun unregisterListenersOfEvent(eventClass: Class<out Event<*>>) {
        registeredListeners[eventClass]?.clear()
    }

    /**
     * Dispatch an event by calling any listener responsible for the given event.
     *
     * @param event An arbitrary instance of any subclass of [Event].
     */
    fun callEvent(event: Event<*>) {
        var scoped = false
        var typed = false

        var scope = ""
        var type = -1

        if (event is Scoped) {
            scoped = true
            scope = (event as Scoped).scope
        }

        if (event is Typed) {
            typed = true
            type = (event as Typed).type
        }

        dispatchEvent(scoped, scope, typed, type, event, ListenerPriority.HIGHEST)
        dispatchEvent(scoped, scope, typed, type, event, ListenerPriority.HIGH)
        dispatchEvent(scoped, scope, typed, type, event, ListenerPriority.NORMAL)
        dispatchEvent(scoped, scope, typed, type, event, ListenerPriority.LOW)
        dispatchEvent(scoped, scope, typed, type, event, ListenerPriority.LOWEST)
    }

    /**
     * Checks for all listeners of an event class, whether they shall receive the event with given parameters.
     *
     * @param scoped Whether the event is scoped.
     * @param scope The event's scope.
     * @param typed Whether the event is typed.
     * @param type The event's type.
     * @param event The event.
     * @param priority The current dispatched priority.
     */
    private fun dispatchEvent(
        scoped: Boolean, scope: String, typed: Boolean, type: Int,
        event: Event<*>, priority: ListenerPriority
    ) {
        val listeners = registeredListeners[event::class.java] ?: return

        for (listener in listeners) {
            if (!(!scoped || listener.scopeGroup.containsScope(scope))) continue
            if (!(!typed || (listener.listenedEventType == -1) || (listener.listenedEventType == type))) continue
            if (listener.priority !== priority) continue

            try {
                listener.listenerMethod.isAccessible = true
                listener.listenerMethod.invoke(listener.listenerClassInstance, event)
            } catch (e: IllegalAccessException) {
              //  logger.error("Could not access event handler method:", throwable = e)
            } catch (e: InvocationTargetException) {
                // depending on error policy, throw an exception or just log and ignore

                when (ERROR_POLICY) {
                    ErrorPolicy.EXCEPTION -> throw EventDispatchException(
                        "Could not dispatch event to handler " +
                                listener.listenerMethod.name, e
                    )

                    ErrorPolicy.LOG -> {
//                        logger.error(
//                            "Could not dispatch event to handler " +
//                                    listener.listenerMethod.name,
//                            throwable = e
//                        )
                    }
                }
            }
        }
    }

    /**
     * A container for listener methods.
     *
     * @param listenerClassInstance Instance of the listener class.
     * @param listenerMethod The event handler method.
     * @param scopeGroup The handler scope (group).
     * @param priority The listener priority.
     * @param listenedEventType The event type for typed events.
     */
    private class Listener(
        val listenerClassInstance: Any, val listenerMethod: Method, val scopeGroup: ScopeGroup,
        val priority: ListenerPriority, val listenedEventType: Int
    )
}