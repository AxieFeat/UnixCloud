package net.unix.api

import java.io.File
import net.unix.api.group.CloudGroup
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.api.modification.module.Module

/**
 * Location space for cloud system.
 */
interface LocationSpace {

    /**
     * Main directory of system.
     */
    val main: File

    /**
     * Directory for logs.
     */
    val logs: File

    /**
     * Directory for [Module]'s.
     */
    val module: File

    /**
     * Directory for [CloudGroup]'s.
     */
    val group: File

    /**
     * Directory for [CloudService]'s.
     */
    val service: File

    /**
     * Directory for [CloudTemplate]'s.
     */
    val template: File
}