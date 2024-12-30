package net.unix.node.event.cloud.template

import net.unix.api.template.Template
import net.unix.event.Event

/**
 * These events call on deletion cloud template.
 */
class TemplateDeleteEvent(
    val template: Template
) : Event<TemplateDeleteEvent>()