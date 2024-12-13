package net.unix.api.group.rule

import net.unix.api.group.CloudGroup
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException

/**
 * This interface represents some behavior rule of group.
 *
 * @param T Type of value.
 */
interface CloudGroupRule<T> : RemoteAccessible {

    /**
     * Group, that's use this rule.
     */
    @get:Throws(RemoteException::class)
    val group: CloudGroup

    /**
     * This function will return [T]. It will be used for [update] function.
     *
     * @return Instance of [T].
     */
    @Throws(RemoteException::class)
    fun get(): T

    /**
     * This functions calls, when rule is updated. You can control group from here.
     *
     * @param input Updated value.
     */
    @Throws(RemoteException::class)
    fun update(input: T)

}