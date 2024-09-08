package net.unix.cloud.event.cloud

import net.kyori.adventure.text.Component
import net.unix.api.event.Cancellable
import net.unix.api.event.Event
import net.unix.api.terminal.logger.Logger

/**
 * Call on [Logger] usage.
 *
 * @param loggerName Logger name.
 * @param format Is formating enabled.
 * @param message Log text.
 * @param throwable Throwable to log.
 */
class CloudTerminalLoggerEvent(
    var loggerName: String,
    var format: Boolean,
    vararg val message: Component,
    var throwable: Throwable?
) : Event<CloudTerminalLoggerEvent>(), Cancellable {

    /**
     * Is cancelled.
     */
    override var cancelled: Boolean = false

}