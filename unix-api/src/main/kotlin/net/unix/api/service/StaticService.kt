package net.unix.api.service

import java.rmi.RemoteException

/**
 * This type of services will not delete after UnixCloud stop.
 *
 * (But can still be deleted with [Service.delete])
 */
interface StaticService : Service {

    /**
     * Is service static.
     *
     * If true - service will not delete after UnixCloud stop.
     * Else - service will be deleted after UnixCloud stop.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var static: Boolean

}