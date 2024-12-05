package net.unix.node.i18n

import net.unix.api.LocationSpace
import net.unix.api.i18n.I18nService
import net.unix.api.i18n.SaveableLocale
import net.unix.api.i18n.SaveableLocaleManager
import net.unix.node.CloudExtension.deserializeComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.yaml.snakeyaml.Yaml
import java.io.File

@Suppress("UNCHECKED_CAST")
class CloudSaveableLocaleManager : SaveableLocaleManager, KoinComponent {

    private val locationSpace: LocationSpace by inject()
    private val i18nService: I18nService by inject()

    override fun loadlAll() {
        locationSpace.language.listFiles()?.filter {
            it.name.endsWith(".yml") ||
                    it.name.endsWith(".yaml") }?.forEach {
            load(it)
        }
    }

    override fun load(file: File): SaveableLocale {
        val line = file.readText()

        val result = Yaml().load<Map<String, Any>>(line).flatten()
            .mapValues { it.value.deserializeComponent() }

        val locale = CloudLocale(
            file.nameWithoutExtension,
            result
        )

        i18nService.register(locale)

        return locale
    }

    private fun Map<String, Any>.flatten(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        fun flatten(currentPrefix: String, map: Map<String, Any>) {
            map.forEach { (key, value) ->
                when (value) {
                    is String -> result["$currentPrefix.$key"] = value
                    is Map<*, *> -> flatten("$currentPrefix.$key", value as Map<String, Any>)
                }
            }
        }

        flatten("", this)
        result.mapKeys { it.key.replaceFirst(".", "") }
        return result
    }

}