package net.unix.api

import java.io.File
import net.unix.api.group.CloudGroup
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.api.modification.module.Module
import net.unix.api.remote.RemoteAccessible
import java.rmi.Remote
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
     * Directory for [CloudGroup]'s.
     */
    @get:Throws(RemoteException::class)
    val group: File

    /**
     * Directory for [CloudService]'s.
     */
    @get:Throws(RemoteException::class)
    val service: File

    /**
     * Directory for [CloudTemplate]'s.
     */
    @get:Throws(RemoteException::class)
    val template: File
}