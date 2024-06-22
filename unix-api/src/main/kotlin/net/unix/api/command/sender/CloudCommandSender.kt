package net.unix.api.command.sender

import net.kyori.adventure.text.Component
import net.unix.api.service.CloudService

/**
 * Класс отправителя команды на сервисах
 */
interface CloudCommandSender : CommandSender {

    /**
     * На каком сервисе игрок находится на данный момент
     */
    val service: CloudService

    /**
     * Лист названий сервисов, на которых игрок бывал ранее,
     * отсортирован по убыванию
     */
    val serviceHistory: List<String>

    /**
     * Отправляет [Component] отправителю команды в чат
     *
     * @param component Компонент для отправки
     */
    fun sendMessage(component: Component)

    /**
     * Отправляет [Component] отправителю команды в actionbar
     *
     * @param component Компонент для отправки
     */
    fun sendActionbar(component: Component)

    /**
     * Отправляет [Component] отправителю команды в title
     *
     * @param title Тайтл
     * @param subtitle Сабтайтл
     * @param fadeIn Сколько времени тайтл должен появляться
     * @param fadeOut Сколько времени тайтл должен исчезать
     */
    fun sendTitle(title: Component, subtitle: Component, fadeIn: Double = 1.0, fadeOut: Double = 1.0)
}

