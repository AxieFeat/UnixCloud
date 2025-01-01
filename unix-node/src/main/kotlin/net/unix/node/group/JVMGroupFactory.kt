package net.unix.node.group

import net.unix.api.group.AutoGroup
import net.unix.api.group.Group
import net.unix.api.group.GroupFactory
import net.unix.api.group.rule.GroupRule
import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.template.Template
import java.util.*

/**
 * It's simple factory of [JVMGroup]'s.
 */
object JVMGroupFactory : GroupFactory {
    override fun create(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        templates: MutableList<Template>,
        wrapper: GroupWrapper?
    ): Group {
        return create(uuid, name, serviceLimit, templates, wrapper, mutableSetOf())
    }

    override fun create(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        templates: MutableList<Template>,
        wrapper: GroupWrapper?,
        rules: MutableSet<GroupRule<Any>>
    ): AutoGroup {
        return JVMGroup(
            uuid, name, serviceLimit, templates, rules, wrapper
        )
    }
}