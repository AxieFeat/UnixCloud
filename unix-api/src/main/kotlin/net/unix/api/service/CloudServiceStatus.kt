package net.unix.api.service

import net.unix.api.remote.RemoteAccessible

/**
 * Status for [CloudService.status].
 */
enum class CloudServiceStatus : RemoteAccessible {

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