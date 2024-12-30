package net.unix.api.remote

import kotlin.reflect.KClass

/**
 * Manager for remote API of UnixCloud.
 */
interface RemoteService {

    /**
     * Register some remote accessible object.
     *
     * @param type Type of object (For getting).
     * @param remoteAccessible Object to register.
     * @param additional Will be added in end of original name.
     */
    fun register(type: KClass<*>, remoteAccessible: RemoteAccessible, additional: String = "")

    /**
     * Start remote server.
     */
    fun start()

}