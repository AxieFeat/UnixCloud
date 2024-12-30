package net.unix.api.service

import net.unix.api.remote.RemoteAccessible

/**
 * Status for [Service.status].
 */
enum class ServiceStatus : RemoteAccessible {

    /**
     * Service is created, but not started.
     */
    PREPARED,

    /**
     * Service is running.
     */
    STARTED,

    /**
     * Service deleted. Deleted services is immutable.
     */
    DELETED;

    companion object
}