package net.unix.node.i18n

import net.unix.api.LocationSpace
import net.unix.api.i18n.I18nService
import net.unix.api.i18n.SaveableLocale
import net.unix.api.i18n.SaveableLocaleManager
import net.unix.node.CloudExtension.deserializeComponent
import net.unix.node.CloudExtension.print
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.yaml.snakeyaml.Yaml
import java.io.File

@Suppress("UNCHECKED_CAST")
class CloudSaveableLocaleManager : SaveableLocaleManager, KoinComponent {

    private val locationSpace: LocationSpace by inject(named("default"))
    private val i18nService: I18nService by inject(named("default"))

    override fun loadAll() {
        locationSpace.language.listFiles()?.filter {
            it.name.endsWith(".yml") ||
                    it.name.endsWith(".yaml") }?.forEach {
            load(it)
        }
    }

    override fun load(file: File): SaveableLocale {
        val line = file.readText()

        val result = Yaml().load<Map<String, String>>(line).flatten().mapValues { it.value.deserializeComponent() }

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

            val prefix = if(currentPrefix == "") "" else "$currentPrefix."

            map.forEach { (key, value) ->
                when (value) {
                    is String -> result["$prefix$key"] = value
                    is Map<*, *> -> flatten("$prefix$key", value as Map<String, Any>)
                }
            }
        }

        flatten("", this)

        return result
    }

}