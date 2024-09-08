package net.unix.api.service

/**
 * Status for [CloudService.status].
 */
enum class CloudServiceStatus {
    PREPARED, STARTED, DELETED;

    companion object
}