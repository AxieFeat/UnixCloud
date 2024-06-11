package net.unix.cloud

import net.unix.api.event.impl.cloud.CloudTerminalCompleteEvent
import net.unix.api.event.listener.EventHandler

class TestListener {

    @EventHandler
    fun onEvent(e: CloudTerminalCompleteEvent) {
        println("event!")
    }

}