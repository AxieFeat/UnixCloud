package net.unix.node.event.cloud.template

import net.unix.api.template.Template
import net.unix.event.Event

/**
 * These events call on creation new instance of template.
 */
class TemplateCreateEvent(
    val template: Template
) : Event<Template>()