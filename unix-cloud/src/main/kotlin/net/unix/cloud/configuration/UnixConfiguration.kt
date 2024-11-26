@file:Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package net.unix.cloud.configuration

import net.unix.api.pattern.Serializable

object UnixConfiguration : CloudConfiguration("/config.json") {

    val fileEncoding = values["file-encoding"].toString()
    val bridge = BridgeSettings.deserialize(values["terminal"])
    val terminal = TerminalSettings.deserialize(values["terminal"])
    val storage = LocationSettings.deserialize(values["storage"])
    val allowedEvents = EventSettings.deserialize(values["allowed-events"])

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["file-encoding"] = fileEncoding
        serialized["bridge"] = bridge.serialize()
        serialized["terminal"] = terminal.serialize()
        serialized["storage"] = storage.serialize()
        serialized["allowed-events"] = allowedEvents.serialize()

        return serialized
    }

    override fun default(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["file-encoding"] = "UTF-8"
        serialized["bridge"] = BridgeSettings().serialize()
        serialized["terminal"] = TerminalSettings().serialize()
        serialized["storage"] = LocationSettings().serialize()
        serialized["allowed-events"] = EventSettings().serialize()

        return serialized
    }
}

data class TerminalSettings(
    val prompt: String = " <white>Unix<gray>@<aqua>cloud<gray>:~<dark_gray># ",
    val consoleName: String = "CONSOLE",
    val serviceCommandPrefix: String = "/",
    val logger: LoggerSettings = LoggerSettings()
) : Serializable {

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["prompt"] = prompt
        serialized["console-name"] = consoleName
        serialized["service-command-prefix"] = serviceCommandPrefix
        serialized["logger"] = logger.serialize()

        return serialized
    }

    companion object {
        fun deserialize(serialized: Map<String, Any>): TerminalSettings {
            val prompt = serialized["prompt"].toString()
            val consoleName = serialized["console-name"].toString()
            val serviceCommandPrefix = serialized["service-command-prefix"].toString()
            val logger = LoggerSettings.deserialize(serialized["logger"])

            return TerminalSettings(
                prompt,
                consoleName,
                serviceCommandPrefix,
                logger
            )
        }

        fun deserialize(any: Any?) = deserialize(any as Map<String, Any>)
    }
}

data class LoggerSettings(
    val format: String = "<gray> {1}</gray> <dark_gray>|</dark_gray> <gray>{2}</gray><dark_gray> »</dark_gray> <gray>{3}</gray><reset>",
    val formatFile: String = " {1} | {2} » {3}",
    val dateFormat: String = "HH:mm:ss",
    val cacheSize: Int = 50
) : Serializable {
    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["format"] = format
        serialized["format-file"] = formatFile
        serialized["date-format"] = dateFormat
        serialized["cache-size"] = 50

        return serialized
    }

    companion object {
        fun deserialize(serialized: Map<String, Any>): LoggerSettings {
            val format = serialized["format"].toString()
            val formatFile = serialized["format-file"].toString()
            val dateFormat = serialized["date-format"].toString()
            val cacheSize = serialized["cache-size"].toString().toIntOrNull() ?: 50

            return LoggerSettings(format, formatFile, dateFormat, cacheSize)
        }

        fun deserialize(any: Any?) = deserialize(any as Map<String, Any>)
    }
}

data class BridgeSettings(
    val port: Int = 9191
) : Serializable {

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["port"] = port

        return serialized
    }

    companion object {
        fun deserialize(serialized: Map<String, Any>): BridgeSettings {
            val port = serialized["port"].toString().toIntOrNull() ?: 9191

            return BridgeSettings(port)
        }

        fun deserialize(any: Any?) = deserialize(any as Map<String, Any>)
    }
}

data class LocationSettings(
    val logs: String = "/logs",
    val extension: String = "/extensions",
    val module: String = "/modules",
    val storage: String = "/storage",
    val group: String = "/group",
    val service: String = "/service",
    val template: String = "/template"
) : Serializable {

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["logs"] = logs
        serialized["extension"] = extension
        serialized["module"] = module
        serialized["storage"] = storage
        serialized["group"] = group
        serialized["service"] = service
        serialized["template"] = template

        return serialized
    }

    companion object {
        fun deserialize(serialized: Map<String, Any>): LocationSettings {
            val logs = serialized["logs"].toString()
            val extensions = serialized["extension"].toString()
            val module = serialized["module"].toString()
            val storage = serialized["storage"].toString()
            val group = serialized["group"].toString()
            val service = serialized["service"].toString()
            val template = serialized["template"].toString()

            return LocationSettings(logs, extensions, module, storage, group, service, template)
        }

        fun deserialize(any: Any?) = deserialize(any as Map<String, Any>)
    }
}

data class EventSettings(
    val events: Map<String, Boolean> = mapOf(
        "AnyEventNameHereToControl" to true,
    )
) : Serializable {

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        events.forEach {
            serialized[it.key] = it.value
        }

        return serialized
    }

    companion object {
        fun deserialize(serialized: Map<String, Any>): EventSettings {
            val events = serialized.mapValues { it.value.toString().toBoolean() }

            return EventSettings(events)
        }

        fun deserialize(any: Any?) = deserialize(any as Map<String, Any>)
    }

}