package net.unix.api.i18n

import net.kyori.adventure.text.Component

/**
 * This interface represents mutable [Locale].
 */
interface MutableLocale : Locale {

    /**
     * Set value in [elements] by key.
     *
     * @param key Key.
     * @param component New element.
     */
    operator fun set(key: String, component: Component)

    /**
     * Set value in [elements] by key.
     *
     * @param key Key.
     * @param component MiniMessage serialized component.
     */
    operator fun set(key: String, component: String)

}