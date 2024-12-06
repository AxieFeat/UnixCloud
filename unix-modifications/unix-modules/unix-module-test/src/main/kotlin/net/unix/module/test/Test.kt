package net.unix.module.test

import net.unix.api.modification.module.annotation.ModuleInfo
import net.unix.node.event.cloud.CloudTerminalCompleteEvent
import net.unix.node.event.listener
import net.unix.node.modification.module.CloudModule

@ModuleInfo(
    name = "Test",
    version = "1.0"
)
class Test : CloudModule() {

    override fun onLoad() {
        registerListener(
            listener<CloudTerminalCompleteEvent> { event ->
                println("Terminal compete event!")
            }
        )
    }

}