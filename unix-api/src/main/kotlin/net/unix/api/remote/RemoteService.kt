package net.unix.api.remote

/**
 * Manager for remote API of UnixCloud.
 */
interface RemoteService {

    /**
     * Register some remote accessible object.
     *
     * @param remoteAccessible Object to register.
     * @param additional Will be added in end of original name.
     */
    fun register(remoteAccessible: RemoteAccessible, additional: String = "")

    /**
     * Start remote server.
     */
    fun start()

}