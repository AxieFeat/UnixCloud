package net.unix.api.group

import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.pattern.Serializable
import net.unix.api.template.Template
import java.util.*

/**
 * This interface represents information about [Group].
 */
interface GroupInfo : Serializable {

    /**
     * The unique id of group.
     */
    val uuid: UUID

    /**
     * Name of group without any formatting.
     */
    val clearName: String

    /**
     * The wrapper of group.
     *
     * If null - type is not set.
     */
    val wrapper: GroupWrapper?

    /**
     * Templates of group.
     *
     * If you change list elements, that will be applied only for new services.
     */
    val templates: MutableList<Template>

    /**
     * Max services count of this group.
     */
    var serviceLimit: Int

}