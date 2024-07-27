package net.unix.api.event.impl.cloud

import net.kyori.adventure.text.Component
import net.unix.api.event.Cancellable
import net.unix.api.event.Event
import net.unix.api.terminal.logger.Logger

/**
 * Call on [Logger] usage
 *
 * @param loggerName Logger name
 * @param format Is formating enabled
 * @param message Log text
 */
class CloudTerminalLoggerEvent(
    var loggerName: String,
    var format: Boolean,
    vararg val message: Component
) : Event<CloudTerminalLoggerEvent>(), Cancellable {

    /**
     * Is cancelled
     */
    override var cancelled: Boolean = false

}