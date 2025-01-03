package net.unix.node.group.wrapper

import net.unix.api.group.wrapper.ConsoleGroupWrapper
import net.unix.api.service.Service
import net.unix.api.service.wrapper.ConsoleServiceWrapper

open class GroupJVMWrapper(
    val startProperties: List<String>,
    override val executableFile: String,
    override val startedLine: String,
    override val stopCommand: String,
) : ConsoleGroupWrapper {

    override fun wrapperFor(service: Service): ConsoleServiceWrapper {
        TODO("Not yet implemented")
    }

    override val name: String = ""

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    companion object {
        @JvmStatic
        private val serialVersionUID = 8519813570919194931L
    }
}