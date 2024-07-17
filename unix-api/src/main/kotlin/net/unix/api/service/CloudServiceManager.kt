package net.unix.api.service

/**
 * Manager for [CloudService]'s
 */
interface CloudServiceManager {
    /**
     * List of current run [CloudService]'s
     */
    val services: List<CloudService>

    /**
     * Get service by name
     *
     * @param name Service name
     *
     * @return [CloudService] instance or null, if not founded
     */
    fun getService(name: String): CloudService?
}