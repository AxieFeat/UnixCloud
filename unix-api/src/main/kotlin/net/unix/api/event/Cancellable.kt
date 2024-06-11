package net.unix.api.event

/**
 * Этот интерфейс следует использовать вместе с [Event], что создавать отменяемые события
 */
interface Cancellable {
    /**
     * Отменён ли ивент
     */
    var cancelled: Boolean
}