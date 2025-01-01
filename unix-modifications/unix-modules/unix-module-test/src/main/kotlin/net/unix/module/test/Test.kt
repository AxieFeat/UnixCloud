package net.unix.module.test

import net.unix.node.event.cloud.CloudTerminalCompleteEvent
import net.unix.node.event.listener
import net.unix.node.modification.module.CloudModule

class Test : CloudModule() {

    override fun onLoad() {
        registerListener(
            listener<CloudTerminalCompleteEvent> { _ ->
                println("Terminal compete event!")
            }
        )
    }

}