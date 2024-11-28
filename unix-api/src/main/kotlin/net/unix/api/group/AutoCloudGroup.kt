package net.unix.api.group

import net.unix.api.group.exception.CloudGroupLimitException
import kotlin.jvm.Throws

/**
 * This interface represents a CloudGroup, that's can automatically control services.
 */
interface AutoCloudGroup : CloudGroup {

    /**
     * The behavior rules of group.
     */
    val rules: MutableSet<CloudGroupRule<Any>>

    /**
     * Set or get current value of startup services.
     *
     * @throws CloudGroupLimitException If [startUpCount] more than [serviceLimit].
     * @throws IllegalArgumentException If [startUpCount] less 0.
     */
    @set:Throws(CloudGroupLimitException::class, IllegalArgumentException::class)
    var startUpCount: Int

}