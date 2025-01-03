package net.unix.api.group.wrapper

import net.unix.api.service.Service
import net.unix.api.service.wrapper.ConsoleServiceWrapper
import java.rmi.RemoteException

/**
 * This interface represents a console wrapper.
 */
interface ConsoleGroupWrapper : GroupWrapper {

    /**
     * Stop command for service.
     */
    @get:Throws(RemoteException::class)
    val stopCommand: String

    /**
     * Trigger-string, the occurrence of which will consider the service started
     */
    @get:Throws(RemoteException::class)
    val startedLine: String

    /**
     * Create console wrapper for service.
     *
     * @param service Service.
     *
     * @return Instance of [ConsoleServiceWrapper].
     */
    @Throws(RemoteException::class)
    override fun wrapperFor(service: Service): ConsoleServiceWrapper

}