package net.unix.event.listener

/**
 * This annotation marks methods, that get called by the manager when an event of the method argument type rises.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class EventHandler(

    /**
     * The priority of the listener method. The highest priority listeners are called first, while lowest priority are
     * called last. By default, priority is set to normal.
     */
    val priority: ListenerPriority = ListenerPriority.NORMAL

)