package net.unix.api.event.listener

/**
 * Эта аннотация отмечает методы, которые вызываются при возникновении события типа, указанного в аргументе метода.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class EventHandler(
    /**
     * Приоритет метода слушателя. Слушатели с наивысшим приоритетом вызываются первыми, а с наименьшим - последними.
     * По умолчанию приоритет установлен на normal.
     *
     * @return Приоритет ивента
     */
    val priority: ListenerPriority = ListenerPriority.NORMAL
)