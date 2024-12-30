package net.unix.api.group

import net.unix.api.group.rule.GroupRule
import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.template.Template
import java.rmi.RemoteException
import java.util.*

/**
 * This interface represents a simple factory of [Group]'s.
 */
interface GroupFactory {

    /**
     * Create new instance of [Group].
     *
     * @param uuid UUID of group.
     * @param name Name of group.
     * @param serviceLimit Limit of service for group.
     * @param templates List of templates.
     * @param wrapper Wrapper of group.
     *
     * @return New instance of [Group].
     *
     * @throws IllegalArgumentException If [name] contain spaces.
     */
    @Throws(IllegalArgumentException::class, RemoteException::class)
    fun create(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        templates: MutableList<Template> = mutableListOf(),
        wrapper: GroupWrapper? = null
    ): Group

    /**
     * Create new instance of [AutoGroup].
     *
     * @param uuid UUID of group.
     * @param name Name of group.
     * @param serviceLimit Limit of service for group.
     * @param templates List of templates.
     * @param wrapper Wrapper of group.
     * @param rules Rules of group.
     *
     * @return New instance of [AutoGroup].
     *
     * @throws IllegalArgumentException If [name] contain spaces.
     */
    @Throws(IllegalArgumentException::class, RemoteException::class)
    fun create(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        templates: MutableList<Template> = mutableListOf(),
        wrapper: GroupWrapper? = null,
        rules: MutableSet<GroupRule<Any>>
    ): AutoGroup

}