package net.unix.node.i18n

import net.unix.api.i18n.I18nService
import net.unix.api.i18n.Locale

object CloudI18nService : I18nService {

    private val cachedLocales = mutableSetOf<Locale>()

    override val locales: Set<Locale>
        get() = cachedLocales

    override lateinit var locale: Locale

    override fun get(name: String): Locale? = cachedLocales.find { it.name == name }

    override fun register(locale: Locale) {
        cachedLocales.add(locale)
    }

    override fun unregister(locale: Locale) {
        cachedLocales.remove(locale)
    }

}