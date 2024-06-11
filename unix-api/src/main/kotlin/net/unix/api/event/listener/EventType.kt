package net.unix.api.event.listener

/**
 * Типизированный слушатель прослушивает только определенный тип типизированного события.
 * Тип определяется целочисленной константой, которая идентифицирует тип
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class EventType(
    /**
     * @return Идентификатор типа, который должен прослушиваться
     */
    val value: Int
)