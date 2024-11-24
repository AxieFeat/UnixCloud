@file:Suppress("UNCHECKED_CAST")

package net.unix.cloud.event

import net.unix.cloud.configuration.UnixConfiguration
import net.unix.cloud.logging.CloudLogger
import net.unix.event.Event
import net.unix.event.EventManager
import net.unix.event.Scoped
import net.unix.event.Typed
import net.unix.event.exception.ErrorPolicy
import net.unix.event.exception.EventDispatchException
import net.unix.event.listener.EventHandler
import net.unix.event.listener.EventType
import net.unix.event.listener.ListenerPriority
import net.unix.event.listener.ListenerScope
import net.unix.event.scope.ScopeGroup
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Create inline listener.
 *
 * @param T Event for listening.
 * @param listener Some your code.
 */
inline fun <reified T> listener(listener: InlineListener<T>) {

    CloudEventManager.registerListeners(
        object {
            @EventHandler
            fun event(event: T) {
                listener.run(event)
            }
        }
    )

}

/**
 * Interface for [listener] function.
 */
fun interface InlineListener<T> {
    fun run(event: T)
}

/**
 * Call event.
 *
 * @return Instance of [T].
 */
fun <T> Event<T>.callEvent(): T {
    CloudEventManager.callEvent(this)

    return this as T
}

/**
 * Registers and manages listeners and handles event dispatching.
 */
object CloudEventManager : EventManager {

    /**
     * The error policy determines how exceptions on dispatched events will be handled.
     */
    private var ERROR_POLICY: ErrorPolicy = ErrorPolicy.LOG

    private val registeredListeners: MutableMap<Class<out Event<*>?>, CopyOnWriteArrayList<Listener>> = mutableMapOf()
    private const val GLOBAL_SCOPE = "*"

    override fun registerListeners(listenerClassInstance: Any) {
        for (method: Method in listenerClassInstance.javaClass.methods) {
            if (!method.isAnnotationPresent(EventHandler::class.java)) {
                continue
            }


            // illegal parameter count
            if (method.parameterCount != 1) {
                CloudLogger.severe("<red>Ignoring illegal event handler: " + method.name +
                        ": Wrong number of arguments (required: 1)")
                continue
            }


            // illegal parameter
            if (!Event::class.java.isAssignableFrom(method.parameterTypes[0])) {
                CloudLogger.severe(
                    ("<red>Ignoring illegal event handler: " + method.name + ": Argument must extend " +
                            Event::class.java.getName())
                )
                continue
            }

            val eventType: Class<out Event<*>?> =
                method.parameterTypes[0] as Class<out Event<*>?>

            var scope: String = GLOBAL_SCOPE
            if (method.isAnnotationPresent(ListenerScope::class.java)) {
                if (!Scoped::class.java.isAssignableFrom(eventType)) {
                    CloudLogger.severe(
                        ("<red>Ignoring illegal event handler: " + method.name +
                                ": Handler is scoped, but event not.")
                    )
                    continue
                }

                scope = method.getAnnotation(ListenerScope::class.java).value
            }

            var listenedEventType = -1
            if (method.isAnnotationPresent(EventType::class.java)) {
                if (!Typed::class.java.isAssignableFrom(eventType)) {
                    CloudLogger.severe(
                        ("<red>Ignoring illegal event handler: " + method.name +
                                ": Handler is typed, but event not.")
                    )
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

    override fun unregisterListeners(listenerClassInstance: Any) {
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

    override fun unregisterListenersOfEvent(eventClass: Class<out Event<*>>) {
        registeredListeners[eventClass]?.clear()
    }

    override fun callEvent(event: Event<*>) {

        if (UnixConfiguration.allowedEvents.events.filter {
            it.key == event.javaClass.simpleName && it.value
        }.isNotEmpty()) return

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
                CloudLogger.severe("Could not access event handler method")
                CloudLogger.exception(e)
            } catch (e: InvocationTargetException) {
                // depending on error policy, throw an exception or just log and ignore

                when (ERROR_POLICY) {
                    ErrorPolicy.EXCEPTION -> throw
                        EventDispatchException(
                            "Could not dispatch event to handler " +
                                listener.listenerMethod.name, e
                        )

                    ErrorPolicy.LOG -> {
                        CloudLogger.severe(
                           "<red>Could not dispatch event to handler " +
                                    listener.listenerMethod.name,
                        )
                        CloudLogger.exception(e)
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