package net.unix.node.i18n

import net.kyori.adventure.text.Component
import net.unix.api.i18n.MutableLocale
import net.unix.api.i18n.SaveableLocale
import net.unix.node.CloudExtension.serialize
import org.yaml.snakeyaml.Yaml
import java.io.File

@Suppress("UNCHECKED_CAST")
open class CloudLocale(
    override val name: String,
    elements: Map<String, Component> = mapOf()
) : MutableLocale, SaveableLocale {

    private val cachedElements: MutableMap<String, Component> = elements.toMutableMap()

    override val elements: Map<String, Component>
        get() = cachedElements

    override fun set(key: String, component: Component) {
        cachedElements[key] = component
    }

    override fun get(key: String): Component? = cachedElements[key]

    override fun serialize(): Map<String, Any> = cachedElements.mapValues {
        it.value.serialize()
    }

    override fun save(file: File) {
        file.writeText(
            Yaml().dump(flatten(cachedElements.mapValues { it.value.serialize() }))
        )
    }

    private fun flatten(flatMap: Map<String, String>): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        flatMap.forEach { (key, value) ->
            var currentMap: MutableMap<String, Any> = result

            val keyParts = key.split(".")

            for (i in 0 until keyParts.size - 1) {
                val partKey = keyParts[i]

                if (!currentMap.contains(partKey)) {
                    currentMap[partKey] = mutableMapOf<String, Any>()
                }

                currentMap = currentMap[partKey] as MutableMap<String, Any>
            }

            currentMap[keyParts.last()] = value
        }

        return result
    }

}