package net.unix.node.event.cloud.template

import net.unix.api.template.CloudTemplate
import net.unix.event.Event

/**
 * These events call on deletion cloud template.
 */
class TemplateDeleteEvent(
    val template: CloudTemplate
) : Event<TemplateDeleteEvent>()