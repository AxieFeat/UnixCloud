package net.unix.api.event.listener

/**
 * A typed listener only listens to a specific type of a typed event. The type is defined by an integer constant, which
 * identifies the type.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class EventType(

    /**
     * @return The type identifier that shall be listened to.
     */
    val value: Int

)