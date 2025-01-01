package net.unix.node.i18n

import net.kyori.adventure.text.Component
import net.unix.api.i18n.I18nService
import net.unix.api.i18n.Locale
import net.unix.node.CloudExtension.format

fun translatable(key: String, vararg argument: Any): Component {
    val component = CloudI18nService.locale[key] ?: Component.text("")

    return component.format(argument)
}

object CloudI18nService : I18nService {

    private val cachedLocales = mutableSetOf<Locale>()

    override val locales: Set<Locale>
        get() = cachedLocales

    override var locale: Locale = ENLocale

    override fun get(name: String): Locale? = cachedLocales.find { it.name == name }

    override fun register(locale: Locale) {
        cachedLocales.add(locale)
    }

    override fun unregister(locale: Locale) {
        cachedLocales.remove(locale)
    }

}