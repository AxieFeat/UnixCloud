package net.unix.node.event.cloud.template

import net.unix.api.template.CloudTemplate
import net.unix.event.Event

/**
 * These events call on creation new instance of template.
 */
class TemplateCreateEvent(
    val template: CloudTemplate
) : Event<CloudTemplate>()