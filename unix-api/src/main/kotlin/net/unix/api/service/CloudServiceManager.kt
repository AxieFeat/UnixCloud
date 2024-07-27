package net.unix.api.service

/**
 * Manager for [CloudService]'s
 */
interface CloudServiceManager {
    /**
     * List of current run [CloudService]'s
     */
    val services: Set<CloudService>

    /**
     * Get [CloudService] by name
     *
     * @param name Service name
     *
     * @return [CloudService] instance or null, if not founded
     */
    operator fun get(name: String): CloudService?

    companion object
}