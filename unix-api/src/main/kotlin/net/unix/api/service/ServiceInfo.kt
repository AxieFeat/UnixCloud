package net.unix.api.service

import net.unix.api.group.Group
import net.unix.api.pattern.Serializable
import java.io.File
import java.util.*

/**
 * This interface represents information about [Service].
 */
interface ServiceInfo : Serializable {

    /**
     * Unique service id.
     */
    val uuid: UUID

    /**
     * Name of service without any formatting.
     */
    val clearName: String

    /**
     * Service group uuid.
     */
    val group: Group

    /**
     * Time of creation this service in milliseconds.
     */
    val created: Long

    /**
     * Service folder.
     *
     * This is where all the runtime files of the service are stored.
     */
    val dataFolder: File

    /**
     * Service status.
     */
    var status: ServiceStatus

}