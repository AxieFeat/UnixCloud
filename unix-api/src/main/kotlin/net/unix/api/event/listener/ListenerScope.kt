package net.unix.api.event.listener

import net.unix.api.event.scope.ScopeGroup

/**
 * Specifies the listener for a certain scope (e.g. A.B) or scope group (e.g. A.*)
 *
 * @see [ScopeGroup]
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class ListenerScope(

    /**
     * The handler's scope or scope group.
     */
    val value: String

)