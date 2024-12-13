package net.unix.api.group

import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.group.rule.CloudGroupRule
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This interface represents a CloudGroup, that's can automatically control services.
 */
interface AutoCloudGroup : CloudGroup {

    /**
     * The behavior rules of group.
     */
    @get:Throws(RemoteException::class)
    val rules: MutableSet<CloudGroupRule<Any>>

    /**
     * Set or get current value of startup services.
     *
     * @throws CloudGroupLimitException If [startUpCount] more than [serviceLimit].
     * @throws IllegalArgumentException If [startUpCount] less 0.
     */
    @set:Throws(CloudGroupLimitException::class, IllegalArgumentException::class, RemoteException::class)
    @get:Throws(RemoteException::class)
    var startUpCount: Int

}