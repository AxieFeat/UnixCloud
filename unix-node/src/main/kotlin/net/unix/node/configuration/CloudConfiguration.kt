package net.unix.node.configuration

import net.unix.api.configuration.Configuration
import net.unix.node.CloudExtension.readJson
import net.unix.node.CloudExtension.toJson
import net.unix.node.mainDirectory
import java.io.File

@Suppress("LeakingThis")
abstract class CloudConfiguration(
    override val location: String
) : Configuration {

    private val configFile = File(mainDirectory, location)

    private var cachedValues: MutableMap<String, Any> = mutableMapOf()

    protected val values: Map<String, Any>
        get() = cachedValues

    private var cachedFirstLoad = false

    val firstLoad: Boolean
        get() = cachedFirstLoad

    init {
        if (!configFile.exists()) {
            configFile.createNewFile()

            val default = default()

            default.toJson(configFile)

            cachedValues = default.toMutableMap()

            cachedFirstLoad = true
        } else {

            cachedValues = configFile.readJson<Map<String, Any>>().toMutableMap()

            cachedFirstLoad = false
        }
    }

    abstract fun default(): Map<String, Any>

    operator fun set(key: String, value: Any) {
        cachedValues[key] = value
    }

    fun save() {
        if (!configFile.exists()) {
            configFile.createNewFile()
        }

        cachedValues.toJson(configFile)
    }
}