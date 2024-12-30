package net.unix.api.group

import net.unix.api.group.exception.GroupLimitException
import net.unix.api.group.rule.GroupRule
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This interface represents a CloudGroup, that's can automatically control services.
 */
interface AutoGroup : Group {

    /**
     * The behavior rules of group.
     */
    @get:Throws(RemoteException::class)
    val rules: MutableSet<GroupRule<Any>>

    /**
     * Set or get current value of startup services.
     *
     * @throws GroupLimitException If [startUpCount] more than [serviceLimit].
     * @throws IllegalArgumentException If [startUpCount] less 0.
     */
    @set:Throws(GroupLimitException::class, IllegalArgumentException::class, RemoteException::class)
    @get:Throws(RemoteException::class)
    var startUpCount: Int

}