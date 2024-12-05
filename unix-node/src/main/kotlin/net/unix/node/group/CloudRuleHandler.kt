package net.unix.node.group

import net.unix.api.group.AutoCloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.api.pattern.Startable
import net.unix.node.logging.CloudLogger
import net.unix.scheduler.impl.scheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CloudRuleHandler : Startable, KoinComponent {

    @delegate:Transient
    private val cloudGroupManager: CloudGroupManager by inject()

    override fun start() {
        scheduler {
            execute(0, 1000) {
                cloudGroupManager.groups.filterIsInstance<AutoCloudGroup>().forEach {

                    it.rules.forEach { rule ->
                        rule.update(rule.get())
                    }

                }
            }
        }
        CloudLogger.info("Started group rule handler")
    }
}