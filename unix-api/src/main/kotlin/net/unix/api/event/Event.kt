package net.unix.api.event

/**
 * Ивент, который может быть вызван и затем отправлен слушателям, чтобы они могли на него отреагировать.
 *
 * @param T Текущий класс ивента
 */
@Suppress("UNCHECKED_CAST")
abstract class Event<T> {

    /**
     * Вызывает текущий ивент
     */
    fun callEvent(): T {
        EventManager.callEvent(this)

        return this as T
    }
}
