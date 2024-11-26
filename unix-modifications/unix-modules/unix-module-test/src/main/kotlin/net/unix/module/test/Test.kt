package net.unix.module.test

import net.unix.cloud.event.cloud.CloudTerminalCompleteEvent
import net.unix.cloud.event.listener
import net.unix.cloud.modification.module.CloudModule

@Suppress("unused")
class Test : CloudModule() {

    override fun onLoad() {
        registerListener(
            listener<CloudTerminalCompleteEvent> { event ->
                println("Terminal compete event!")
            }
        )
    }

}