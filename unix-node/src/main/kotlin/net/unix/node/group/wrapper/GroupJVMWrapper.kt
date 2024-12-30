package net.unix.node.group.wrapper

import net.unix.api.group.wrapper.ConsoleGroupWrapper
import net.unix.api.service.Service
import net.unix.api.service.wrapper.ConsoleServiceWrapper
import net.unix.node.service.ServiceJVMWrapper
import java.io.File

@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
open class GroupJVMWrapper(
    val startProperties: List<String>,
    override val executableFile: String,
    override val stopCommand: String,
) : ConsoleGroupWrapper {

    override val name: String = "JVM"

    override fun executableFor(service: Service): ConsoleServiceWrapper {
        return ServiceJVMWrapper(
            service = service,
            executableFile = File(service.dataFolder, executableFile),
            stopCommand = stopCommand
        )
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["name"] = name
        serialized["start-properties"] = startProperties
        serialized["executable-file"] = executableFile
        serialized["stop-command"] = stopCommand

        return serialized
    }

    companion object {

        fun deserialize(serialized: Map<String, Any>): GroupJVMWrapper {
            val startProperties = serialized["start-properties"] as? List<String>
            val executableFile = serialized["executable-file"].toString()
            val stopCommand = serialized["stop-command"].toString()

            return GroupJVMWrapper(
                startProperties ?: listOf("java", "-Xms100M", "-Xmx1G", "-jar"),
                executableFile,
                stopCommand
            )
        }
    }

}