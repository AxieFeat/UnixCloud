package net.unix.api.event.impl.cloud

import net.unix.api.event.Cancellable
import net.unix.api.event.Event

class CloudTerminalLoggerEvent(
    var loggerName: String,
    var format: Boolean,
    var message: String
) : Event<CloudTerminalLoggerEvent>(), Cancellable {

    override var cancelled: Boolean = false

}