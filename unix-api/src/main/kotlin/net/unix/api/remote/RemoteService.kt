package net.unix.api.remote

interface RemoteService {

    fun register(remoteAccessible: RemoteAccessible, additional: String = "")

    fun start()

}