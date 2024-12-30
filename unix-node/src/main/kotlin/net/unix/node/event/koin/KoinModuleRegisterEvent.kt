package net.unix.node.event.koin

import net.unix.event.Cancellable
import net.unix.event.Event
import org.koin.core.module.Module

class KoinModuleRegisterEvent(
    val modules: MutableList<Module>
) : Event<KoinModuleRegisterEvent>(), Cancellable {

    override var cancelled: Boolean = false

}