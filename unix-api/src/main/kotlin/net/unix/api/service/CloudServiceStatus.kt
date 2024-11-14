package net.unix.api.service

/**
 * Status for [CloudService.status].
 */
enum class CloudServiceStatus {
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