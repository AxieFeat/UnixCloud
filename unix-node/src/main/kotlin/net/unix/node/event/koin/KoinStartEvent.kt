package net.unix.node.event.koin

import net.unix.event.Event
import org.koin.core.KoinApplication

class KoinStartEvent(
    val koin: KoinApplication
) : Event<KoinStartEvent>()