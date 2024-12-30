package net.unix.api

import java.io.File
import net.unix.api.group.Group
import net.unix.api.service.Service
import net.unix.api.template.Template
import net.unix.api.modification.module.Module
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * Location space for cloud system.
 */
interface LocationSpace : RemoteAccessible {

    /**
     * Main directory of system.
     */
    @get:Throws(RemoteException::class)
    val main: File

    /**
     * Directory for logs.
     */
    @get:Throws(RemoteException::class)
    val logs: File

    /**
     * Directory for languages.
     */
    @get:Throws(RemoteException::class)
    val language: File

    /**
     * Directory for [Module]'s.
     */
    @get:Throws(RemoteException::class)
    val module: File

    /**
     * Directory for [Group]'s.
     */
    @get:Throws(RemoteException::class)
    val group: File

    /**
     * Directory for [Service]'s.
     */
    @get:Throws(RemoteException::class)
    val service: File

    /**
     * Directory for [Template]'s.
     */
    @get:Throws(RemoteException::class)
    val template: File
}