package net.unix.api.i18n

import net.kyori.adventure.text.Component
import net.unix.api.pattern.Nameable

/**
 * This interface represents some locale.
 */
interface Locale : Nameable {

    /**
     * All elements of locale.
     */
    val elements: Map<String, Component>

    /**
     * Name of locale.
     */
    override val name: String

    /**
     * Get locale element by it key.
     *
     * @param key Key.
     *
     * @return Instance of [Component] or null, if not founded
     */
    operator fun get(key: String): Component?
}