package net.unix.cloud.configuration

import net.unix.api.configuration.Configuration
import net.unix.cloud.CloudExtension.readJson
import net.unix.cloud.CloudExtension.toJson
import net.unix.cloud.mainDirectory
import java.io.File

@Suppress("LeakingThis")
abstract class CloudConfiguration(
    override val location: String
) : Configuration {

    private val configFile = File(mainDirectory, location)

    private var cachedValues: Map<String, Any> = mapOf()
    protected val values: Map<String, Any>
        get() = cachedValues

    init {
        if (!configFile.exists()) {
            configFile.createNewFile()

            val default = default()

            default.toJson(configFile)

            cachedValues = default
        } else {

            cachedValues = configFile.readJson<Map<String, Any>>()

        }
    }

    abstract fun default(): Map<String, Any>

}