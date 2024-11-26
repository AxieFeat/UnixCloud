package net.unix.cloud.event.koin

import net.unix.event.Event
import org.koin.core.KoinApplication

class KoinStartEvent(
    val koin: KoinApplication
) : Event<KoinStartEvent>()