package net.unix.api.i18n

/**
 * This interface represents locale manage.
 */
interface I18nService {

    /**
     * Set of all registered locales.
     */
    val locales: Set<Locale>

    /**
     * Currently selected locale.
     */
    var locale: Locale

    /**
     * Get locale.
     *
     * @param name Local name.
     *
     * @return Instance of [Locale] or null, if not founded.
     */
    operator fun get(name: String): Locale?

    /**
     * Register locale in [locales].
     *
     * @param locale Locale to register.
     */
    fun register(locale: Locale)

    /**
     * Unregister locale from [locales]
     *
     * @param locale Locale to unregister.
     */
    fun unregister(locale: Locale)
}