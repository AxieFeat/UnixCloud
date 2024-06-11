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
 * Регистрирует и управляет слушателями и обрабатывает диспетчеризацию событий
 */
object EventManager {
    /**
     * Политика ошибок определяет, как будут обрабатываться исключения из диспетчеризованных событий.
     */
    private var ERROR_POLICY: ErrorPolicy = ErrorPolicy.LOG

    private val registeredListeners: MutableMap<Class<out Event<*>?>, CopyOnWriteArrayList<Listener>> = mutableMapOf()
    private const val GLOBAL_SCOPE = "*"

    /**
     * Определяет и регистрирует все методы [EventHandler] в классе данного экземпляра.
     * Экземпляр будет сохранен, поскольку он используется для вызова слушателей, поэтому слушатели не могут быть статическими,
     * а объект не будет уничтожен сборкой мусора. Используйте эту функцию для отмены регистрации слушателей
     *
     * @param listenerClassInstance Объект класса, содержащего обработчики событий
     */
    fun registerListeners(listenerClassInstance: Any) {
        for (method: Method in listenerClassInstance.javaClass.methods) {
            if (!method.isAnnotationPresent(EventHandler::class.java)) {
                continue
            }


            // illegal parameter count
            if (method.parameterCount != 1) {
                System.err.println(
                    "Ignoring illegal event handler: " + method.name +
                            ": Wrong number of arguments (required: 1)"
                )
                continue
            }


            // illegal parameter
            if (!Event::class.java.isAssignableFrom(method.parameterTypes[0])) {
                System.err.println(
                    ("Ignoring illegal event handler: " + method.name + ": Argument must extend " +
                            Event::class.java.getName())
                )
                continue
            }

            val eventType: Class<out Event<*>?> =
                method.parameterTypes[0] as Class<out Event<*>?>

            var scope: String = GLOBAL_SCOPE
            if (method.isAnnotationPresent(ListenerScope::class.java)) {
                if (!Scoped::class.java.isAssignableFrom(eventType)) {
                    System.err.println(
                        ("Ignoring illegal event handler: " + method.name +
                                ": Handler is scoped, but event not.")
                    )
                    continue
                }

                scope = method.getAnnotation(ListenerScope::class.java).value
            }

            var listenedEventType = -1
            if (method.isAnnotationPresent(EventType::class.java)) {
                if (!Typed::class.java.isAssignableFrom(eventType)) {
                    System.err.println(
                        ("Ignoring illegal event handler: " + method.name +
                                ": Handler is typed, but event not.")
                    )
                    continue
                }

                listenedEventType = method.getAnnotation(EventType::class.java).value
            }

            val priority: ListenerPriority = method.getAnnotation(EventHandler::class.java).priority
            val scopeGroup: ScopeGroup = ScopeGroup(scope)

            val listener = Listener(listenerClassInstance, method, scopeGroup, priority, listenedEventType)
            addListener(eventType, listener)
        }
    }

    /**
     * Добавляет слушателя в мапу зарегистрированных слушателей
     *
     * @param eventType Тип события, которое должен обрабатывать слушатель
     * @param listener Метод слушателя
     */
    private fun addListener(eventType: Class<out Event<*>?>, listener: Listener) {
        if (!registeredListeners.containsKey(eventType)) registeredListeners[eventType] =
            CopyOnWriteArrayList()

        registeredListeners[eventType]!!.add(listener)
    }

    /**
     * Снимает с регистрации все обработчики событий, связанные с этим объектом
     *
     * @param listenerClassInstance Объект класса, содержащего обработчики событий, который был зарегистрирован в обработчике событий
     */
    fun unregisterListeners(listenerClassInstance: Any) {
        for (listenerList: CopyOnWriteArrayList<Listener> in registeredListeners.values) {
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
     * Снимает с регистрации все обработчики событий, связанные с данным типом ивента
     *
     * @param eventClass Класс ивента
     */
    fun unregisterListenersOfEvent(eventClass: Class<out Event<*>?>) {
        registeredListeners[eventClass]!!.clear()
    }

    /**
     * Отправляйте событие, вызывая любой слушатель, ответственный за данное событие
     *
     * @param event Произвольный экземпляр любого подкласса [Event]
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
     * Проверяет для всех слушателей класса события, должны ли они получить событие с заданными параметрами
     *
     * @param scoped   whether the event is scoped
     * @param scope    the event's scope
     * @param typed    whether the event is typed
     * @param type     the event's type
     * @param event    the event
     * @param priority the current dispatched priority
     */
    private fun dispatchEvent(
        scoped: Boolean, scope: String, typed: Boolean, type: Int,
        event: Event<*>, priority: ListenerPriority
    ) {
        val listeners = registeredListeners[event::class.java]
        if (listeners != null) {
            for (listener: Listener in listeners) {
                if (!scoped || listener.scopeGroup.containsScope(scope)) {
                    if (!typed || (listener.listenedEventType == -1) || (listener.listenedEventType == type)) {
                        if (listener.priority === priority) {
                            try {
                                listener.listenerMethod.isAccessible = true
                                listener.listenerMethod.invoke(listener.listenerClassInstance, event)
                            } catch (e: IllegalAccessException) {
                                System.err.print("Could not access event handler method:")
                                e.printStackTrace()
                            } catch (e: InvocationTargetException) {
                                // depending on error policy, throw an exception or just log and ignore

                                when (ERROR_POLICY) {
                                    ErrorPolicy.EXCEPTION -> throw EventDispatchException(
                                        "Could not dispatch event to handler " +
                                                listener.listenerMethod.name, e
                                    )

                                    ErrorPolicy.LOG -> {
                                        System.err.println(
                                            "Could not dispatch event to handler " +
                                                    listener.listenerMethod.name
                                        )
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Контейнер для методов слушателей
     */
    private class Listener(
        val listenerClassInstance: Any, val listenerMethod: Method, scopeGroup: ScopeGroup,
        priority: ListenerPriority, listenedEventType: Int
    ) {
        val scopeGroup: ScopeGroup
        val priority: ListenerPriority
        val listenedEventType: Int

        /**
         * Создайте контейнер для методов слушателей со всеми необходимыми метаданными
         *
         * @param listenerClassInstance instance of the listener class
         * @param listenerMethod        the event handler method
         * @param scopeGroup            the handler scope (group)
         * @param priority              the listener priority
         * @param listenedEventType     the event type for typed events
         */
        init {
            this.scopeGroup = scopeGroup
            this.priority = priority
            this.listenedEventType = listenedEventType
        }
    }
}