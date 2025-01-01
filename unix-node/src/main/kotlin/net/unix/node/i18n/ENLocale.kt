package net.unix.node.i18n

import net.unix.api.LocationSpace
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File

object ENLocale : CloudLocale("en"), KoinComponent {

    private val locationSpace: LocationSpace by inject(named("default"))

    init {

        set("start.built", "UnixCloud successfully built with {1} extensions")

        save(File(locationSpace.language, "en.yml"))
    }

}