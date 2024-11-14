package net.unix.api.service

import net.unix.api.group.CloudGroup
import java.util.UUID

/**
 * Manager for [CloudService]'s.
 *
 * With this you can control the running [CloudService]'s.
 * To start a new service, use [CloudGroup.create]
 */
interface CloudServiceManager {

    /**
     * List of current run [CloudService]'s.
     */
    val services: Set<CloudService>

    /**
     * Register service in [services].
     *
     * @param service Service to register.
     */
    fun register(service: CloudService)

    /**
     * Is exist [CloudService]'s with one name.
     *
     * @param name Name for check.
     *
     * @return True, if count of [CloudService]'s with this name > 1, else false
     */
    fun duplicates(name: String): Boolean = get(name).count() > 1

    /**
     * Get [CloudService] by name.
     *
     * @param name Service name.
     *
     * @return List of [CloudService]'s with this name, can be empty.
     */
    operator fun get(name: String): List<CloudService>

    /**
     * Get [CloudService] by [UUID].
     *
     * @param uuid The uuid.
     *
     * @return Instance of [CloudService] or null, if not founded.
     */
    operator fun get(uuid: UUID): CloudService?

    companion object
}