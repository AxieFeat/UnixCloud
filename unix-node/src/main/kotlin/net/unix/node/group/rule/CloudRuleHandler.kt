package net.unix.node.group.rule

import net.unix.api.group.AutoGroup
import net.unix.api.group.GroupManager
import net.unix.api.pattern.Startable
import net.unix.node.logging.CloudLogger
import net.unix.scheduler.impl.scheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * It's rule handler for [AutoGroup]'s.
 *
 * All the rules are updated here every second
 */
object CloudRuleHandler : Startable, KoinComponent {

    @delegate:Transient
    private val groupManager: GroupManager by inject(named("default"))

    override fun start() {
        scheduler {
            execute(0, 1000) {
                // Find AutoGroup instances.
                groupManager.groups.filterIsInstance<AutoGroup>().forEach {

                    // Update all rules of group.
                    it.rules.forEach { rule ->
                        rule.update(rule.get())
                    }

                }
            }
        }
        CloudLogger.info("Started group rule handler")
    }
}